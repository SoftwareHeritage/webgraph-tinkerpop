package org.webgraph.tinkerpop.structure.provider;

import org.webgraph.tinkerpop.structure.property.edge.EdgeProperty;
import org.webgraph.tinkerpop.structure.property.vertex.VertexProperty;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Enables the user to provide access to vertex and edge properties.
 *
 * @implNote All registered properties are associated with every vertex/edge. Vertices not associated with a property are assumed to store a null value.
 */
public class SimpleWebGraphPropertyProvider implements WebGraphPropertyProvider {

    private final Map<String, VertexProperty> vertexProperties = new HashMap<>();
    private final Map<String, EdgeProperty> edgeProperties = new HashMap<>();
    private Function<Long, String> vertexLabeller = id -> "vertex";
    private BiFunction<Long, Long, String> edgeLabeller = (from, to) -> "edge";

    /**
     * Defines the label of a vertex.
     *
     * @param labeller a function, which accepts a vertex id and returns the string label of that vertex.
     */
    public void setVertexLabeller(Function<Long, String> labeller) {
        this.vertexLabeller = labeller;
    }

    @Override
    public String vertexLabel(long vertexId) {
        return vertexLabeller.apply(vertexId);
    }

    /**
     * Register a vertex property in the provider. TinkerPop will be able to request this property by key.
     *
     * @param vertexProperty the property to register in the provider.
     * @see VertexProperty
     */
    public void addVertexProperty(VertexProperty vertexProperty) {
        if (vertexProperties.put(vertexProperty.getKey(), vertexProperty) != null) {
            throw new IllegalArgumentException("Key already exists: " + vertexProperty.getKey());
        }
    }

    @Override
    public String[] vertexProperties(long vertexId) {
        return vertexProperties.keySet().toArray(String[]::new);
    }

    @Override
    public Object vertexProperty(String key, long nodeId) {
        VertexProperty vertexProperty = vertexProperties.get(key);
        if (vertexProperty == null) {
            return null;
        }
        return vertexProperty.get(nodeId);
    }

    /**
     * Defines the label of an edge.
     *
     * @param labeller a function, which accepts two vertex ids and returns the string label of the edge, defined by these vertices.
     */
    public void setEdgeLabeller(BiFunction<Long, Long, String> labeller) {
        this.edgeLabeller = labeller;
    }

    @Override
    public String edgeLabel(long fromId, long toId) {
        return edgeLabeller.apply(fromId, toId);
    }

    /**
     * Register an edge property in the provider. TinkerPop will be able to request this property by key.
     *
     * @param edgeProperty the property to register in the provider.
     * @see EdgeProperty
     */
    public void addEdgeProperty(EdgeProperty edgeProperty) {
        if (edgeProperties.put(edgeProperty.getKey(), edgeProperty) != null) {
            throw new IllegalArgumentException("Key already exists: " + edgeProperty.getKey());
        }
    }

    @Override
    public String[] edgeProperties(long fromId, long toId) {
        return edgeProperties.keySet().toArray(String[]::new);
    }

    @Override
    public Object edgeProperty(String key, long fromId, long toId) {
        EdgeProperty edgeProperty = edgeProperties.get(key);
        if (edgeProperty == null) {
            return null;
        }
        return edgeProperty.get(fromId, toId);
    }
}
