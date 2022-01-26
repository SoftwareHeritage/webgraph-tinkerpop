package org.webgraph.tinkerpop.structure.property;

public interface PropertyHandler<V> {
    /**
     * Returns the value associated with this node, or {@code null} if the value is empty.
     *
     * @param id the id of the node
     * @return the associated value if it is present, or {@code null} otherwise
     */
    V get(long id);
}
