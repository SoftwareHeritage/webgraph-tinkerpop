package org.webgraph.tinkerpop.query;

import it.unimi.dsi.big.util.MappedFrontCodedStringBigList;
import it.unimi.dsi.big.webgraph.labelling.ArcLabelledImmutableGraph;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.softwareheritage.graph.Graph;
import org.softwareheritage.graph.labels.DirEntry;
import org.webgraph.tinkerpop.structure.property.edge.ArcLabelEdgeProperty;
import org.webgraph.tinkerpop.structure.property.edge.ArcLabelEdgeSubProperty;
import org.webgraph.tinkerpop.structure.property.edge.ArcLabelEdgeSubPropertyGetter;
import org.webgraph.tinkerpop.structure.property.vertex.VertexProperty;
import org.webgraph.tinkerpop.structure.property.vertex.file.FileVertexProperty;
import org.webgraph.tinkerpop.structure.provider.SimpleWebGraphPropertyProvider;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class SwhProperties {

    private static MappedFrontCodedStringBigList edgeLabelNames;

    private static void loadEdgeLabelNames(String path) throws IOException {
        try {
            edgeLabelNames = MappedFrontCodedStringBigList.load(path + ".labels.fcl");
        } catch (ConfigurationException e) {
            throw new IOException(e);
        }
    }

    public static SimpleWebGraphPropertyProvider getProvider(Graph graph) throws IOException {
        String path = graph.getPath();
        loadEdgeLabelNames(path);
        SimpleWebGraphPropertyProvider provider = new SimpleWebGraphPropertyProvider();
        provider.setVertexLabeller(id -> graph.getNodeType(id).toString());
        provider.addVertexProperty(new FileVertexProperty<>("author_timestamp", Long.class,
                Path.of(path + ".property.author_timestamp.bin")));
        provider.addVertexProperty(new VertexProperty<>("swhid", graph::getSWHID));
        ArcLabelEdgeProperty<DirEntry[]> edgeProperty = new ArcLabelEdgeProperty<>(
                (ArcLabelledImmutableGraph) graph.getGraph().getForwardGraph());
        provider.addEdgeProperty(edgeProperty);
        provider.addEdgeProperty(new ArcLabelEdgeSubProperty<>("dir_entry_str", edgeProperty, dirEntryStr()));
        provider.addEdgeProperty(new ArcLabelEdgeSubProperty<>("filenames", edgeProperty, filenames()));
        provider.addEdgeProperty(new ArcLabelEdgeSubProperty<>("filename", edgeProperty, singleFilename()));
        return provider;
    }

    private static ArcLabelEdgeSubPropertyGetter<DirEntry[], DirEntryString[]> dirEntryStr() {
        return dirEntries -> {
            if (dirEntries.length == 0) {
                return null;
            }
            DirEntryString[] res = new DirEntryString[dirEntries.length];
            for (int i = 0; i < dirEntries.length; i++) {
                res[i] = new DirEntryString(getFilename(dirEntries[i]), dirEntries[i].permission);
            }
            return res;
        };
    }

    private static ArcLabelEdgeSubPropertyGetter<DirEntry[], List<String>> filenames() {
        return dirEntries -> {
            if (dirEntries.length == 0) {
                return null;
            }
            List<String> res = new ArrayList<>();
            for (DirEntry dirEntry : dirEntries) {
                res.add(getFilename(dirEntry));
            }
            return res;
        };
    }

    private static ArcLabelEdgeSubPropertyGetter<DirEntry[], String> singleFilename() {
        return dirEntries -> {
            if (dirEntries.length != 1) {
                return null;
            }
            return getFilename(dirEntries[0]);
        };
    }

    private static String getFilename(DirEntry dirEntry) {
        return new String(Base64.getDecoder().decode(edgeLabelNames.getArray(dirEntry.filenameId)));
    }

    public static class DirEntryString {

        public String filename;
        public int permission;

        public DirEntryString(String filename, int permission) {
            this.filename = filename;
            this.permission = permission;
        }
    }
}
