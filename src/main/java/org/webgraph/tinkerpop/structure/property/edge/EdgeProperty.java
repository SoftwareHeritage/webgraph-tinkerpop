package org.webgraph.tinkerpop.structure.property.edge;

/**
 * Defines an edge property of a graph.
 */
public class EdgeProperty {

    private final String key;
    private final EdgePropertyGetter propertyGetter;

    /**
     * Created a new edge property with the given key and value extractor.
     *
     * @param key            the string key of the property
     * @param propertyGetter a function which receives two vertex ids and returns a value associated with that edge.
     */
    public EdgeProperty(String key, EdgePropertyGetter propertyGetter) {
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
     * Gets the value of this property for the given edge.
     *
     * @param fromId the outgoing vertex id.
     * @param toId   the incoming vertex id.
     * @return the value of the property.
     */
    public Object get(long fromId, long toId) {
        return propertyGetter.get(fromId, toId);
    }
}
