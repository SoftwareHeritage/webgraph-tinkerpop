package org.webgraph.tinkerpop.structure.property.vertex.file.type;

import it.unimi.dsi.fastutil.longs.LongBigList;
import it.unimi.dsi.util.ByteBufferLongBigList;
import org.webgraph.tinkerpop.structure.property.vertex.VertexPropertyGetter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;

/**
 * Vertex property getter for file property of type {@code Long}.
 * <p>
 * Expects the property file to store a {@code LongBigList} with indices corresponding to vertex ids.
 * A value of {@link Long#MIN_VALUE}  corresponds to empty value.
 *
 * @implNote Uses {@link ByteBufferLongBigList#map} to read the file.
 */
public class LongFileVertexPropertyGetter implements VertexPropertyGetter {
    private final LongBigList list;

    /**
     * Constructs a property getter from file path.
     *
     * @param path the path to the property file.
     * @throws IOException if an I/O error occurs
     */
    public LongFileVertexPropertyGetter(Path path) throws IOException {
        this.list = ByteBufferLongBigList.map(new FileInputStream(path.toFile()).getChannel());
    }

    @Override
    public Long get(long vertexId) {
        long res = list.getLong(vertexId);
        if (res == Long.MIN_VALUE) {
            return null;
        }
        return res;
    }
}
