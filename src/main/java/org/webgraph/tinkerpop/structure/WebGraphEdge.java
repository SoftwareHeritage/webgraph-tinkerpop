package org.webgraph.tinkerpop.structure;

import it.unimi.dsi.fastutil.longs.LongLongImmutablePair;
import it.unimi.dsi.fastutil.longs.LongLongPair;
import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Property;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.util.ElementHelper;
import org.apache.tinkerpop.gremlin.structure.util.StringFactory;
import org.apache.tinkerpop.gremlin.util.iterator.IteratorUtils;

import java.util.Iterator;

public class WebGraphEdge extends WebGraphElement implements Edge {

    public WebGraphEdge(long fromId, long toId, WebGraphGraph graph) {
        super(new LongLongImmutablePair(fromId, toId), graph.getLabelProvider().edgeLabel(fromId, toId), graph);
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
    public <V> Iterator<Property<V>> properties(String... propertyKeys) {
        String[] keys = propertyKeys.length == 0 ? graph.getEdgePropertyKeys() : propertyKeys; // if no props are provided, return all props
        return new Iterator<>() {
            int nextIndex = -1;
            Property<V> nextProp = nextProp();

            @Override
            public boolean hasNext() {
                return nextIndex < keys.length;
            }

            @Override
            public Property<V> next() {
                Property<V> res = nextProp;
                nextProp = nextProp();
                return res;
            }

            private Property<V> nextProp() {
                nextIndex++;
                while (nextIndex < keys.length) {
                    String key = keys[nextIndex];
                    LongLongPair id = (LongLongPair) id();
                    V val = graph.getEdgeProperty(key, id.firstLong(), id.secondLong());
                    if (val != null) {
                        return new WebGraphProperty<>(WebGraphEdge.this, key, val);
                    }
                    nextIndex++;
                }
                return null;
            }
        };
    }

    @Override
    public <V> Property<V> property(String key, V value) {
        return null;
    }

    @Override
    public void remove() {
        throw Edge.Exceptions.edgeRemovalNotSupported();
    }

    @Override
    public String toString() {
        return StringFactory.edgeString(this);
    }

    @Override
    public int hashCode() {
        return ElementHelper.hashCode(this);
    }

    @Override
    public boolean equals(Object obj) {
        return ElementHelper.areEqual(this, obj);
    }
}
