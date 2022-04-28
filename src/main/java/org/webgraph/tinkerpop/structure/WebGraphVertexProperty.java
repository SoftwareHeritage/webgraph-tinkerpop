package org.webgraph.tinkerpop.structure;

import org.apache.tinkerpop.gremlin.structure.Property;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.VertexProperty;

import java.util.Iterator;

public class WebGraphVertexProperty<V> extends WebGraphProperty<V> implements VertexProperty<V> {
    private final WebGraphVertex vertex;

    public WebGraphVertexProperty(WebGraphVertex vertex, String key, V value) {
        super(vertex, key, value);
        this.vertex = vertex;
    }

    @Override
    public Vertex element() {
        return this.vertex;
    }

    @Override
    public Object id() {
        // sourced from Neo4j implementation
        return (long) (this.key.hashCode() + this.value.hashCode() + this.vertex.id().hashCode());
    }

    @Override
    public <U> Property<U> property(String key, U value) {
        throw VertexProperty.Exceptions.metaPropertiesNotSupported();
    }

    @Override
    public <U> Iterator<Property<U>> properties(String... propertyKeys) {
        throw VertexProperty.Exceptions.metaPropertiesNotSupported();
    }
}
