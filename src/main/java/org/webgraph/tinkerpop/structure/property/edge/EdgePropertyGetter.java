package org.webgraph.tinkerpop.structure.property.edge;

@FunctionalInterface
public interface EdgePropertyGetter {
    Object get(long fromId, long toId);
}
