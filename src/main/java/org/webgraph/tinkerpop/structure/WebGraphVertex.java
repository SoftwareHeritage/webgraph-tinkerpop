package org.webgraph.tinkerpop.structure;

import it.unimi.dsi.big.webgraph.LazyLongIterator;
import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.VertexProperty;
import org.apache.tinkerpop.gremlin.structure.util.StringFactory;
import org.apache.tinkerpop.gremlin.util.iterator.IteratorUtils;

import java.util.Iterator;

public class WebGraphVertex extends WebGraphElement implements Vertex {

    public WebGraphVertex(long id, WebGraphGraph graph) {
        super(id, graph.getSettings().vertexLabel(id), graph);
    }

    @Override
    public Iterator<Vertex> vertices(Direction direction, String... edgeLabels) {
        // ignores edge labels
        switch (direction) {
            case OUT:
                return successors();
            case IN:
                return predecessors();
            default:
                return IteratorUtils.concat(successors(), predecessors());
        }
    }

    private Iterator<Vertex> predecessors() {
        return toNativeIterator(graph.getBaseGraph().predecessors((Long) id()));
    }

    private Iterator<Vertex> successors() {
        return toNativeIterator(graph.getBaseGraph().successors((Long) id()));
    }

    private Iterator<Vertex> toNativeIterator(LazyLongIterator source) {
        return new Iterator<>() {
            long next = source.nextLong();

            @Override
            public boolean hasNext() {
                return next == -1;
            }

            @Override
            public Vertex next() {
                long res = next;
                next = source.nextLong();
                return new WebGraphVertex(res, graph);
            }
        };
    }

    @Override
    public Iterator<Edge> edges(Direction direction, String... edgeLabels) {
        switch (direction) {
            case OUT:
                return outEdges(edgeLabels);
            case IN:
                return inEdges(edgeLabels);
            default:
                return IteratorUtils.concat(outEdges(edgeLabels), inEdges(edgeLabels));
        }
    }

    private Iterator<Edge> outEdges(String... edgeLabels) {
        Iterator<Vertex> out = vertices(Direction.OUT, edgeLabels);
        return IteratorUtils.map(out, to1 -> new WebGraphEdge((long) id(), (long) to1.id(), graph));
    }

    private Iterator<Edge> inEdges(String... edgeLabels) {
        Iterator<Vertex> in = vertices(Direction.IN, edgeLabels);
        return IteratorUtils.map(in, from1 -> new WebGraphEdge((long) from1.id(), (long) id(), graph));
    }

    @Override
    public <V> VertexProperty<V> property(VertexProperty.Cardinality cardinality, String key, V value, Object... keyValues) {
        throw new UnsupportedOperationException("Vertex property creation is not supported");
    }

    @Override
    public <V> Iterator<VertexProperty<V>> properties(String... propertyKeys) {
        return new Iterator<>() {
            int nextIndex = -1;
            VertexProperty<V> nextProp = nextProp();

            @Override
            public boolean hasNext() {
                return nextIndex < propertyKeys.length;
            }

            @Override
            public VertexProperty<V> next() {
                VertexProperty<V> res = nextProp;
                nextProp = nextProp();
                return res;
            }

            private VertexProperty<V> nextProp() {
                nextIndex++;
                while (nextIndex < propertyKeys.length) {
                    String key = propertyKeys[nextIndex];
                    V val = graph.getProperty(key, (long) id());
                    if (val != null) {
                        return new WebGraphVertexProperty<>(WebGraphVertex.this, key, val);
                    }
                    nextIndex++;
                }
                return null;
            }
        };
    }

    @Override
    public Edge addEdge(String label, Vertex inVertex, Object... keyValues) {
        throw Vertex.Exceptions.edgeAdditionsNotSupported();
    }

    @Override
    public void remove() {
        throw Vertex.Exceptions.vertexRemovalNotSupported();
    }

    @Override
    public String toString() {
        return StringFactory.vertexString(this);
    }
}
