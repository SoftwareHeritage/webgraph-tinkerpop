package org.webgraph.tinkerpop.structure.property.vertex;

public interface VertexProperty {
    String getKey();

    Object get(long vertexId);
}
