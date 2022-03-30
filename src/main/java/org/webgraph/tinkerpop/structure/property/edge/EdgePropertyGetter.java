package org.webgraph.tinkerpop.structure.property.edge;

@FunctionalInterface
public interface EdgePropertyGetter<T> {
    T get(long fromId, long toId);
}
