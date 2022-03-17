package org.webgraph.tinkerpop.structure.property.vertex;

/**
 * Defines a vertex property of a graph.
 */
public class VertexProperty {

    private final String key;
    private final VertexPropertyGetter propertyGetter;

    /**
     * Created a new vertex property with the given key and value extractor.
     *
     * @param key            the string key of the property
     * @param propertyGetter a function which receives a vertex id and returns a value associated with that vertex.
     */
    public VertexProperty(String key, VertexPropertyGetter propertyGetter) {
        this.key = key;
        this.propertyGetter = propertyGetter;
    }

    /**
     * Gets the string key of this property.
     *
     * @return the string key of the property.
     */
    public String getKey() {
        return key;
    }

    /**
     * Gets the value of this property for the given vertex.
     *
     * @param vertexId the id of the vertex.
     * @return the value of the property.
     */
    public Object get(long vertexId) {
        return propertyGetter.get(vertexId);
    }
}
