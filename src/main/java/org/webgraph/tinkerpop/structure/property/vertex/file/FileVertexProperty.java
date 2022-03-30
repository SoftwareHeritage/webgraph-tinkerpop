package org.webgraph.tinkerpop.structure.property.vertex.file;

import org.webgraph.tinkerpop.structure.property.vertex.VertexProperty;
import org.webgraph.tinkerpop.structure.property.vertex.VertexPropertyGetter;
import org.webgraph.tinkerpop.structure.property.vertex.file.type.LongFileVertexPropertyGetter;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Vertex property provider for properties stored in a file.
 * Based on the type of the property it uses one of the handlers.
 *
 * @implNote currently supports only {@code Long} type.
 * @see LongFileVertexPropertyGetter
 */
public class FileVertexProperty<T> extends VertexProperty<T> {

    /**
     * @param key  the string key of the property
     * @param type the type of the property, which defines which handler should be used.
     * @param path the path to property file
     * @throws IOException if an I/O error occurs
     */
    public FileVertexProperty(String key, Class<T> type, Path path) throws IOException {
        super(key, getFilePropertyGetterForType(type, path));
    }

    private static <T> VertexPropertyGetter<T> getFilePropertyGetterForType(Class<T> type, Path path) throws IOException {
        if (type == Long.class) {
            return (VertexPropertyGetter<T>) new LongFileVertexPropertyGetter(path);
        } else {
            throw new RuntimeException("Unsupported property type: " + type.getSimpleName());
        }
    }

}
