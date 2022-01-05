package org.webgraph.tinkerpop.structure;

import it.unimi.dsi.big.webgraph.LazyLongIterator;
import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.VertexProperty;
import org.apache.tinkerpop.gremlin.structure.util.StringFactory;
import org.apache.tinkerpop.gremlin.util.iterator.IteratorUtils;

import java.util.Iterator;
import java.util.stream.Stream;

public class WebGraphVertex extends WebGraphElement implements Vertex {

    public WebGraphVertex(long id, WebGraphGraph graph) {
        super(id, graph.getSettings().vertexLabel(id), graph);
    }

    @Override
    public Iterator<Vertex> vertices(Direction direction, String... edgeLabels) {
        switch (direction) {
            case OUT:
                return toIterator(graph.getBaseGraph().successors((Long) id()));
            case IN:
                return toIterator(graph.getBaseGraph().predecessors((Long) id()));
            default:
                LazyLongIterator successors = graph.getBaseGraph().successors((Long) id());
                Iterator<Vertex> out = toIterator(successors);
                LazyLongIterator predecessors = graph.getBaseGraph().predecessors((Long) id());
                Iterator<Vertex> in = toIterator(predecessors);
                return IteratorUtils.concat(out, in);
        }
    }

    private Iterator<Vertex> toIterator(LazyLongIterator source) {
        return Stream.generate(source::nextLong)
                     .takeWhile(s -> s != -1)
                     .map(s -> (Vertex) new WebGraphVertex(s, graph))
                     .iterator();
    }

    @Override
    public Iterator<Edge> edges(Direction direction, String... edgeLabels) {
        switch (direction) {
            case OUT:
                Iterator<Vertex> out = vertices(Direction.OUT, edgeLabels);
                return IteratorUtils.map(out, to1 -> new WebGraphEdge((long) id(), (long) to1.id(), graph));
            case IN:
                Iterator<Vertex> in = vertices(Direction.IN, edgeLabels);
                return IteratorUtils.map(in, from1 -> new WebGraphEdge((long) from1.id(), (long) id(), graph));
            default:
                Iterator<Vertex> in2 = vertices(Direction.IN, edgeLabels);
                Iterator<Edge> inE = IteratorUtils.map(in2, from -> new WebGraphEdge((long) from.id(), (long) id(), graph));
                Iterator<Vertex> out2 = vertices(Direction.OUT, edgeLabels);
                Iterator<Edge> outE = IteratorUtils.map(out2, to -> new WebGraphEdge((long) id(), (long) to.id(), graph));
                return IteratorUtils.concat(outE, inE);
        }
    }

    @Override
    public <V> VertexProperty<V> property(VertexProperty.Cardinality cardinality, String key, V value, Object... keyValues) {
        throw new UnsupportedOperationException("Vertex properties not supported");
    }

    @Override
    public <V> Iterator<VertexProperty<V>> properties(String... propertyKeys) {
        throw new UnsupportedOperationException("Vertex properties not supported");
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
