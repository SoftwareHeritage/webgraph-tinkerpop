package org.webgraph.tinkerpop.structure.property.vertex;

@FunctionalInterface
public interface VertexPropertyGetter<T> {
    T get(long vertexId);
}
