package org.webgraph.tinkerpop.structure.property;

import it.unimi.dsi.util.ByteBufferLongBigList;

import java.io.FileInputStream;
import java.io.IOException;

public class LongVertexPropertyHandler implements VertexPropertyHandler<Long> {

    private final ByteBufferLongBigList props;

    public LongVertexPropertyHandler(String path) throws IOException {
        props = ByteBufferLongBigList.map(new FileInputStream(path).getChannel());
    }

    @Override
    public Long get(long id) {
        long value = props.getLong(id);
        return value == Long.MIN_VALUE ? null : value;
    }
}
