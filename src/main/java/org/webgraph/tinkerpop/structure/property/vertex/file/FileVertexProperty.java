package org.webgraph.tinkerpop.structure.property.vertex.file;

import org.webgraph.tinkerpop.structure.property.vertex.VertexProperty;
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
public class FileVertexProperty extends VertexProperty {

    /**
     * @param key  the string key of the property
     * @param type the type of the property, which defines which handler should be used.
     * @param path the path to property file
     * @throws IOException if an I/O error occurs
     */
    public FileVertexProperty(String key, Class<?> type, Path path) throws IOException {
        super(key, getFilePropertyGetterForType(type, path));
    }

    private static LongFileVertexPropertyGetter getFilePropertyGetterForType(Class<?> type, Path path) throws IOException {
        if (type == Long.class) {
            return new LongFileVertexPropertyGetter(path);
        } else {
            throw new RuntimeException("Unsupported property type: " + type.getSimpleName());
        }
    }

}
