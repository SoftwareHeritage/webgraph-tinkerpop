package org.webgraph.tinkerpop.structure;

import org.apache.tinkerpop.gremlin.structure.Element;
import org.apache.tinkerpop.gremlin.structure.Graph;

public abstract class WebGraphElement implements Element {

    private final Object id;
    private final String label;
    protected final WebGraphGraph graph;

    public WebGraphElement(Object id, String label, WebGraphGraph graph) {
        this.id = id;
        this.label = label;
        this.graph = graph;
    }

    @Override
    public Object id() {
        return id;
    }

    @Override
    public String label() {
        return label;
    }

    @Override
    public Graph graph() {
        return graph;
    }

}
