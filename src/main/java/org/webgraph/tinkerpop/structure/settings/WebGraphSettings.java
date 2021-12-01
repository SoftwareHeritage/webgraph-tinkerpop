package org.webgraph.tinkerpop.structure.settings;

/**
 * The interface provides methods to define edge and vertex labels for a WebGraph graph.
 */
public interface WebGraphSettings {
    /**
     * Returns the label associated with a vertex
     *
     * @param vertexId the id of the vertex
     * @return the associated label
     */
    String vertexLabel(long vertexId);

    /**
     * Returns the label associated with an edge
     *
     * @param outId the id of the outgoing vertex (tail of the edge)
     * @param inId  the id of the in vertex (head of the edge)
     * @return the associated label
     */
    String edgeLabel(long outId, long inId);
}
