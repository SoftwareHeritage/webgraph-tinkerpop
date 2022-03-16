package org.webgraph.tinkerpop.structure.provider;

import org.webgraph.tinkerpop.structure.property.edge.EdgeProperty;
import org.webgraph.tinkerpop.structure.property.vertex.VertexProperty;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Provides stub labels for WebGraph edges and vertices.
 */
public class SimpleWebGraphPropertyProvider implements WebGraphPropertyProvider {

    private final Map<String, VertexProperty> vertexProperties = new HashMap<>();
    private final Map<String, EdgeProperty> edgeProperties = new HashMap<>();
    private Function<Long, String> vertexLabeller = id -> "vertex";
    private BiFunction<Long, Long, String> edgeLabeller = (from, to) -> "edge";

    public void addVertexProperty(VertexProperty vertexProperty) {
        if (vertexProperties.put(vertexProperty.getKey(), vertexProperty) != null) {
            throw new IllegalArgumentException("Key already exists: " + vertexProperty.getKey());
        }
    }

    public void setVertexLabeller(Function<Long, String> labeller) {
        this.vertexLabeller = labeller;
    }

    public void setEdgeLabeller(BiFunction<Long, Long, String> labeller) {
        this.edgeLabeller = labeller;
    }

    public void addEdgeProperty(EdgeProperty edgeProperty) {
        if (edgeProperties.put(edgeProperty.getKey(), edgeProperty) != null) {
            throw new IllegalArgumentException("Key already exists: " + edgeProperty.getKey());
        }
    }

    @Override
    public String vertexLabel(long vertexId) {
        return vertexLabeller.apply(vertexId);
    }

    @Override
    public String[] vertexProperties(long nodeId) {
        return vertexProperties.keySet().toArray(String[]::new);
    }

    @Override
    public Object vertexProperty(String key, long nodeId) {
        VertexProperty vertexProperty = vertexProperties.get(key);
        if (vertexProperty == null) {
            throw new IllegalArgumentException("No property named: " + key);
        }
        return vertexProperty.get(nodeId);
    }

    @Override
    public String[] edgeProperties(long fromId, long toId) {
        return edgeProperties.keySet().toArray(String[]::new);
    }

    @Override
    public String edgeLabel(long fromId, long toId) {
        return edgeLabeller.apply(fromId, toId);
    }

    @Override
    public Object edgeProperty(String key, long fromId, long toId) {
        EdgeProperty edgeProperty = edgeProperties.get(key);
        if (edgeProperty == null) {
            throw new IllegalArgumentException("No property named: " + key);
        }
        return edgeProperty.get(fromId, toId);
    }
}
