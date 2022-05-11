package org.webgraph.tinkerpop.structure;

import org.apache.tinkerpop.gremlin.structure.Element;
import org.apache.tinkerpop.gremlin.structure.Property;

import java.util.NoSuchElementException;

public class WebGraphProperty<V> implements Property<V> {
    protected final String key;
    protected final V value;
    private final WebGraphElement element;

    public WebGraphProperty(WebGraphElement element, String key, V value) {
        this.element = element;
        this.key = key;
        this.value = value;
    }

    @Override
    public String key() {
        return this.key;
    }

    @Override
    public V value() throws NoSuchElementException {
        if (!isPresent()) {
            throw new NoSuchElementException();
        }
        return this.value;
    }

    @Override
    public boolean isPresent() {
        return this.value != null;
    }

    @Override
    public Element element() {
        return this.element;
    }

    @Override
    public void remove() {
        throw Property.Exceptions.propertyRemovalNotSupported();
    }

}
