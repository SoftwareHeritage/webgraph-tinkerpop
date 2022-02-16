package org.webgraph.tinkerpop.structure;

import it.unimi.dsi.big.webgraph.ImmutableGraph;
import it.unimi.dsi.big.webgraph.LazyLongIterator;
import it.unimi.dsi.big.webgraph.LazyLongIterators;
import it.unimi.dsi.big.webgraph.NodeIterator;
import it.unimi.dsi.fastutil.longs.LongLongPair;
import org.apache.commons.configuration2.BaseConfiguration;
import org.apache.commons.configuration2.Configuration;
import org.apache.tinkerpop.gremlin.process.computer.GraphComputer;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Transaction;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.util.wrapped.WrappedGraph;
import org.apache.tinkerpop.gremlin.util.iterator.IteratorUtils;
import org.softwareheritage.graph.BidirectionalImmutableGraph;
import org.webgraph.tinkerpop.structure.property.EdgePropertyHandler;
import org.webgraph.tinkerpop.structure.property.LongVertexPropertyHandler;
import org.webgraph.tinkerpop.structure.property.VertexPropertyHandler;
import org.webgraph.tinkerpop.structure.settings.DefaultWebGraphLabelProvider;
import org.webgraph.tinkerpop.structure.settings.WebGraphLabelProvider;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class WebGraphGraph implements Graph, WrappedGraph<ImmutableGraph> {
    private static final String GRAPH_PATH = "webgraph.path";

    private final BidirectionalImmutableGraph graph;
    private final Configuration configuration;
    private final WebGraphLabelProvider settings;
    private final Map<String, VertexPropertyHandler<?>> vertexPropertyHandlers = new HashMap<>();
    private final Map<String, EdgePropertyHandler<?>> edgePropertyHandlers = new HashMap<>();
    private final Map<String, Class<?>> vertexPropTypes;
    private final Map<String, Class<?>> edgePropTypes;

    private WebGraphGraph(String path, Map<String, Class<?>> vertexPropTypes, Map<String, Class<?>> edgePropTypes, Configuration configuration) throws IOException {
        this(new BidirectionalImmutableGraph(ImmutableGraph.load(path), ImmutableGraph.load(path + "-transposed")),
                new DefaultWebGraphLabelProvider(), vertexPropTypes, edgePropTypes, configuration);
    }

    private WebGraphGraph(BidirectionalImmutableGraph graph, WebGraphLabelProvider settings, Map<String, Class<?>> vertexPropTypes, Map<String, Class<?>> edgePropTypes, Configuration configuration) {
        this.configuration = configuration;
        this.graph = graph;
        this.settings = settings;
        this.vertexPropTypes = vertexPropTypes;
        this.edgePropTypes = edgePropTypes;
    }

    public static WebGraphGraph open(BidirectionalImmutableGraph graph, WebGraphLabelProvider settings, Map<String, Class<?>> vertexPropTypes, Map<String, Class<?>> edgePropTypes, String path) {
        Configuration config = EMPTY_CONFIGURATION();
        config.setProperty(GRAPH_PATH, path);
        return new WebGraphGraph(graph, settings, vertexPropTypes, edgePropTypes, config);
    }

    @Override
    public Iterator<Vertex> vertices(Object... vertexIds) {
        if (vertexIds.length == 0) {
            return IteratorUtils.map(graph.nodeIterator(), id -> new WebGraphVertex(id, this));
        }
        return new Iterator<>() {
            int nextIndex = 0;

            @Override
            public boolean hasNext() {
                return nextIndex < vertexIds.length;
            }

            @Override
            public Vertex next() {
                Object id = vertexIds[nextIndex];
                if (!(id instanceof Number)) {
                    throw new IllegalArgumentException("Expected id of numeric type.");
                }
                nextIndex++;
                return new WebGraphVertex(((Number) id).longValue(), WebGraphGraph.this);
            }
        };
    }

    @Override
    public Iterator<Edge> edges(Object... edgeIds) {
        if (edgeIds.length == 0) { // returns all graph edges
            // Iterate over all 'froms' and return an edge for each neighbour.
            return new Iterator<>() {
                NodeIterator froms;
                long nextFrom;
                LazyLongIterator tos;
                long nextTo;

                WebGraphEdge nextEdge = nextEdge();

                private WebGraphEdge nextEdge() {
                    if (froms == null) { // first run, initialize fields
                        froms = graph.nodeIterator();
                        nextFrom = nextFrom();
                        tos = nextFrom == -1 ? LazyLongIterators.EMPTY_ITERATOR : graph.successors(nextFrom);
                        nextTo = tos.nextLong();
                    }
                    while (nextTo == -1) { // no more successors for this node
                        nextFrom = nextFrom();
                        if (nextFrom == -1) { // no more 'from' nodes
                            return null;
                        }
                        tos = graph.successors(nextFrom);
                        nextTo = tos.nextLong();
                    }
                    return new WebGraphEdge(nextFrom, nextTo, WebGraphGraph.this);
                }

                @Override
                public boolean hasNext() {
                    return nextEdge != null;
                }

                @Override
                public Edge next() {
                    WebGraphEdge next = nextEdge;
                    nextEdge = nextEdge();
                    return next;
                }

                private long nextFrom() {
                    return froms.hasNext() ? froms.nextLong() : -1;
                }
            };
        }
        return new Iterator<>() {
            int nextIndex = 0;

            @Override
            public boolean hasNext() {
                return nextIndex < edgeIds.length;
            }

            @Override
            public Edge next() {
                Object idObj = edgeIds[nextIndex];
                if (!(idObj instanceof LongLongPair)) {
                    throw new IllegalArgumentException("Expected id of numeric type.");
                }
                LongLongPair id = (LongLongPair) idObj;
                nextIndex++;
                return new WebGraphEdge(id.firstLong(), id.secondLong(), WebGraphGraph.this);
            }
        };
    }

    @Override
    public Vertex addVertex(Object... keyValues) {
        throw Graph.Exceptions.vertexAdditionsNotSupported();
    }

    @Override
    public <C extends GraphComputer> C compute(Class<C> graphComputerClass) throws IllegalArgumentException {
        throw Graph.Exceptions.graphComputerNotSupported();
    }

    @Override
    public GraphComputer compute() throws IllegalArgumentException {
        throw Graph.Exceptions.graphComputerNotSupported();
    }

    @Override
    public Transaction tx() {
        throw Graph.Exceptions.transactionsNotSupported();
    }

    @Override
    public void close() {
    }

    @Override
    public Variables variables() {
        throw Graph.Exceptions.variablesNotSupported();
    }

    @Override
    public Configuration configuration() {
        return configuration;
    }

    public static Configuration EMPTY_CONFIGURATION() {
        return new BaseConfiguration() {{
            this.setProperty(Graph.GRAPH, WebGraphGraph.class.getName());
        }};
    }

    public static WebGraphGraph open(Map<String, Class<?>> vertexPropTypes, Map<String, Class<?>> edgePropTypes, Configuration configuration) throws IOException {
        String path = configuration.getString(GRAPH_PATH);
        return new WebGraphGraph(path, vertexPropTypes, edgePropTypes, configuration);
    }

    public static WebGraphGraph open(String path, Map<String, Class<?>> vertexPropTypes, Map<String, Class<?>> edgePropTypes) throws IOException {
        Configuration config = EMPTY_CONFIGURATION();
        config.setProperty(GRAPH_PATH, path);
        return open(vertexPropTypes, edgePropTypes, config);
    }

    public static WebGraphGraph open(String path) throws IOException {
        return open(path, new HashMap<>(), new HashMap<>());
    }

    public <V> V getVertexProperty(String key, long id) {
        if (!vertexPropertyHandlers.containsKey(key)) {
            if (!vertexPropTypes.containsKey(key)) {
                throw new IllegalArgumentException("Property type not specified for key: " + key);
            }
            Class<?> propType = vertexPropTypes.get(key);
            String path = getPropertyFilePath(key);
            vertexPropertyHandlers.put(key, newVertexPropertyHandler(propType, path));
        }
        return (V) vertexPropertyHandlers.get(key).get(id);
    }

    public <V> V getEdgeProperty(String key, long from, long to) {
        if (!edgePropertyHandlers.containsKey(key)) {
            if (!edgePropertyHandlers.containsKey(key)) {
                throw new IllegalArgumentException("Property type not specified for key: " + key);
            }
            Class<?> propType = edgePropTypes.get(key);
            String path = getPropertyFilePath(key);
            edgePropertyHandlers.put(key, newEdgePropertyHandler(propType, path));
        }
        return (V) edgePropertyHandlers.get(key).get(from, to);
    }

    private VertexPropertyHandler<?> newVertexPropertyHandler(Class<?> propType, String path) {
        try {
            if (propType == Long.class) {
                return new LongVertexPropertyHandler(path);
            }
            throw new IllegalArgumentException("Unknown property type: " + propType.getSimpleName());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private EdgePropertyHandler<?> newEdgePropertyHandler(Class<?> propType, String path) {
        throw new UnsupportedOperationException("Edge property handlers not supported");
    }

    private String getPropertyFilePath(String propKey) {
        String graphPath = configuration.getString(GRAPH_PATH);
        if (graphPath == null) {
            throw new IllegalStateException("No graph path is specified, cannot access properties");
        }
        return String.format("%s-%s.bin", graphPath, propKey);
    }

    public String[] getPropertyKeys() {
        return vertexPropTypes.keySet().toArray(String[]::new);
    }

    public String[] getEdgePropertyKeys() {
        return edgePropTypes.keySet().toArray(String[]::new);
    }

    @Override
    public BidirectionalImmutableGraph getBaseGraph() {
        return graph;
    }

    public WebGraphLabelProvider getLabelProvider() {
        return settings;
    }
}
