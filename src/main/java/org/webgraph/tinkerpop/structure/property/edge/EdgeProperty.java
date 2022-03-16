package org.webgraph.tinkerpop.structure.property.edge;

public interface EdgeProperty {
    String getKey();

    Object get(long fromId, long toId);
}
