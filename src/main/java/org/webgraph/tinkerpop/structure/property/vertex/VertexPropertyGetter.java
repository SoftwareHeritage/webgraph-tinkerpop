package org.webgraph.tinkerpop.structure.property.vertex;

@FunctionalInterface
public interface VertexPropertyGetter {
    Object get(long vertexId);
}
