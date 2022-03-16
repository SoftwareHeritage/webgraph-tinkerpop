package org.webgraph.tinkerpop.structure.property.vertex.file.type;

import it.unimi.dsi.fastutil.longs.LongBigList;
import it.unimi.dsi.util.ByteBufferLongBigList;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;

public class LongPropertyGetter implements PropertyGetter {
    private final LongBigList list;

    public LongPropertyGetter(Path path) throws IOException {
        this.list = ByteBufferLongBigList.map(new FileInputStream(path.toFile()).getChannel());
    }

    @Override
    public Long get(long nodeId) {
        long res = list.getLong(nodeId);
        if (res == Long.MIN_VALUE) {
            return null;
        }
        return res;
    }
}
