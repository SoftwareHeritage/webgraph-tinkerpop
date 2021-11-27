package org.webgraph.tinkerpop.structure;

import it.unimi.dsi.fastutil.longs.LongLongImmutablePair;
import it.unimi.dsi.fastutil.longs.LongLongPair;
import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Property;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.util.StringFactory;
import org.apache.tinkerpop.gremlin.util.iterator.IteratorUtils;

import java.util.Iterator;

public class WebGraphEdge extends WebGraphElement implements Edge {

    public static final String LABEL = "edge";

    public WebGraphEdge(long fromId, long toId, WebGraphGraph graph) {
        super(new LongLongImmutablePair(fromId, toId), LABEL, graph);
    }

    @Override
    public Iterator<Vertex> vertices(Direction direction) {
        LongLongPair edge = (LongLongPair) id();
        switch (direction) {
            case OUT:
                return IteratorUtils.of(new WebGraphVertex(edge.firstLong(), graph));
            case IN:
                return IteratorUtils.of(new WebGraphVertex(edge.secondLong(), graph));
            default:
                return IteratorUtils.of(new WebGraphVertex(edge.firstLong(), graph),
                        new WebGraphVertex(edge.secondLong(), graph));
        }
    }

    @Override
    public <V> Property<V> property(String key, V value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <V> Iterator<Property<V>> properties(String... propertyKeys) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void remove() {
        throw Edge.Exceptions.edgeRemovalNotSupported();
    }

    @Override
    public String toString() {
        return StringFactory.edgeString(this);
    }
}
