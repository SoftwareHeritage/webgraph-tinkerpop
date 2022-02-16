package org.webgraph.tinkerpop.structure.property;

public interface EdgePropertyHandler<V> {
    /**
     * Returns the value associated with this edge, or {@code null} if the value is empty.
     *
     * @param fromId the id of the from node
     * @param toId   the id of the to node
     * @return the associated value if it is present, or {@code null} otherwise
     */
    V get(long fromId, long toId);
}
