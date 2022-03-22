package org.webgraph.tinkerpop.structure.property.vertex.file.type;

import it.unimi.dsi.fastutil.bytes.ByteBigList;
import it.unimi.dsi.fastutil.bytes.ByteMappedBigList;
import it.unimi.dsi.fastutil.longs.LongBigList;
import it.unimi.dsi.fastutil.longs.LongMappedBigList;
import org.webgraph.tinkerpop.structure.property.vertex.VertexPropertyGetter;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;

/**
 * Vertex property getter for file property of type {@code String}.
 * <p>
 * Expects the property file to store a {@code ByteBigList} buffer and {@code LongBigList} with offsets.
 * Offsets correspond to node ids. At the given offset the buffer stores 4 bytes for the length of the message
 * in bytes then the string bytes.
 */
public class StringFileVertexPropertyGetter implements VertexPropertyGetter {
    private final ByteBigList buffer;
    private final LongBigList offsets;

    /**
     * Constructs a property getter from buffer and offset file paths
     *
     * @param bufferPath the path to the buffer file with string lengths and bytes.
     * @param offsetPath the path to the file with buffer offsets for each vertex.
     * @throws IOException if an I/O error occurs
     */
    public StringFileVertexPropertyGetter(Path bufferPath, Path offsetPath) throws IOException {
        try (RandomAccessFile bufferFile = new RandomAccessFile(bufferPath.toFile(), "r");
             RandomAccessFile offsetFile = new RandomAccessFile(offsetPath.toFile(), "r")) {
            this.buffer = ByteMappedBigList.map(bufferFile.getChannel());
            this.offsets = LongMappedBigList.map(offsetFile.getChannel());
        }
    }

    @Override
    public String get(long vertexId) {
        long offset = offsets.getLong(vertexId);
        byte[] lengthBytes = new byte[Integer.BYTES];
        buffer.getElements(offset, lengthBytes, 0, Integer.BYTES);
        int length = bytesToInt(lengthBytes);

        byte[] stringBytes = new byte[length];
        buffer.getElements(offset + Integer.BYTES, stringBytes, 0, length);
        return new String(stringBytes);
    }

    private int bytesToInt(byte[] b) {
        int result = 0;
        for (int i = 0; i < Integer.BYTES; i++) {
            result <<= Byte.SIZE;
            result |= (b[i] & 0xFF);
        }
        return result;
    }
}
