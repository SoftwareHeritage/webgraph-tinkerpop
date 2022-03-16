package org.webgraph.tinkerpop.structure.property.vertex.file;

import org.webgraph.tinkerpop.structure.property.vertex.VertexProperty;
import org.webgraph.tinkerpop.structure.property.vertex.file.type.LongPropertyGetter;
import org.webgraph.tinkerpop.structure.property.vertex.file.type.PropertyGetter;

import java.io.IOException;
import java.nio.file.Path;

public class FileVertexProperty implements VertexProperty {

    private final String key;
    private final PropertyGetter propertyGetter;

    public FileVertexProperty(String key, Class<?> type, Path path) throws IOException {
        this.key = key;
        if (type == Long.class) {
            propertyGetter = new LongPropertyGetter(path);
        } else {
            throw new RuntimeException("Unsupported property type: " + type.getSimpleName());
        }
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public Object get(long vertexId) {
        return propertyGetter.get(vertexId);
    }

}
