package org.webgraph.tinkerpop.query;

import org.apache.tinkerpop.gremlin.process.traversal.P;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import java.util.HashSet;
import java.util.function.Function;

public class Gremlin {

    /**
     * Finds all leaves of a graph (vertices with no outgoing edges).
     *
     * @implNote uses DFS to traverse the graph, keeps visited vertices in a {@code HashSet}.
     */
    public static GraphTraversal<Vertex, Vertex> leaves(GraphTraversalSource g) {
        return g.withSideEffect("a", new HashSet<>())
                .V().not(__.in())
                .repeat(__.out().dedup().where(P.without("a")).aggregate("a"))
                .until(__.not(__.out()));
    }

    /**
     * Find all parents of the provided vertex, which have the provided label.
     *
     * @param v     the id of the starting vertex.
     * @param label the label used to filter parents
     * @return parent vertices with the provided label.
     */
    public static Function<GraphTraversalSource, GraphTraversal<Vertex, Vertex>> parentsWithLabel(long v, String label) {
        return g -> g.withSideEffect("a", new HashSet<>())
                     .V(v)
                     .repeat(__.in().dedup().where(P.without("a")).aggregate("a"))
                     .emit(__.hasLabel(label)).dedup();
    }

}
