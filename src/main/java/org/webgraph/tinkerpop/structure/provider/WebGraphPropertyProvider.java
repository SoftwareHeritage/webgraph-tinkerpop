package org.webgraph.tinkerpop.structure.provider;

/**
 * This interface defines methods to be provided to the TinkerPop implementation.
 * The methods include fetching vertex/edge labels and properties, as well as associated property keys.
 */
public interface WebGraphPropertyProvider {

    /**
     * Returns the label associated with a node.
     * A node can only have one label.
     *
     * @param vertexId the id of the vertex
     * @return the associated label
     */
    String vertexLabel(long vertexId);

    /**
     * Returns keys of all properties, available for this node.
     *
     * @param vertexId the id of the vertex
     * @return the associated label
     */
    String[] vertexProperties(long vertexId);

    /**
     * Returns the value of the property associated with a node.
     *
     * @param key      the key of the property
     * @param vertexId the id of the vertex
     * @return the value of the property, or null of no value is present
     */
    Object vertexProperty(String key, long vertexId);

    /**
     * Returns keys of all properties, available for this arc.
     *
     * @param fromId the id of the outgoing vertex (tail of the edge)
     * @param toId   the id of the in vertex (head of the edge)
     * @return the associated label
     */
    String[] edgeProperties(long fromId, long toId);

    /**
     * Returns the label associated with an arc.
     * An edge can only have one label.
     *
     * @param fromId the id of the outgoing vertex (tail of the edge)
     * @param toId   the id of the in vertex (head of the edge)
     * @return the associated label
     */
    String edgeLabel(long fromId, long toId);

    /**
     * Returns the value of the property associated with an arc.
     *
     * @param key    the key of the property
     * @param fromId the id of the outgoing vertex
     * @param toId   the id of the in vertex
     * @return the value of the property, or null of no value is present
     */
    Object edgeProperty(String key, long fromId, long toId);

}
