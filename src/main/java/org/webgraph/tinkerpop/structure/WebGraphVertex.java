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

    public static final String LABEL = "vertex";

    public WebGraphVertex(long id, WebGraphGraph graph) {
        super(id, LABEL, graph);
    }

    @Override
    public Iterator<Vertex> vertices(Direction direction, String... edgeLabels) {
        LazyLongIterator successors = graph.getBaseGraph().successors((Long) id());
        Iterator<Vertex> out = Stream.generate(successors::nextLong)
                                     .takeWhile(s -> s != -1)
                                     .map(s -> (Vertex) new WebGraphVertex(s, graph))
                                     .iterator();
        LazyLongIterator predecessors = graph.getTransposedGraph().successors((Long) id());
        Iterator<Vertex> in = Stream.generate(predecessors::nextLong)
                                    .takeWhile(s -> s != -1)
                                    .map(s -> (Vertex) new WebGraphVertex(s, graph))
                                    .iterator();
        switch (direction) {
            case OUT:
                return out;
            case IN:
                return in;
            default:
                return IteratorUtils.concat(out, in);
        }
    }

    @Override
    public Iterator<Edge> edges(Direction direction, String... edgeLabels) {
        Iterator<Vertex> in = vertices(Direction.IN, edgeLabels);
        Iterator<Edge> inE = IteratorUtils.map(in, from -> new WebGraphEdge((long) from.id(), (long) id(), graph));
        Iterator<Vertex> out = vertices(Direction.OUT, edgeLabels);
        Iterator<Edge> outE = IteratorUtils.map(out, to -> new WebGraphEdge((long) id(), (long) to.id(), graph));
        switch (direction) {
            case OUT:
                return outE;
            case IN:
                return inE;
            default:
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
