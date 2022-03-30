package org.webgraph.tinkerpop.structure.property.edge;

@FunctionalInterface
public interface ArcLabelEdgeSubPropertyGetter<E, T> {
    T get(E arcLabelProperty);
}
