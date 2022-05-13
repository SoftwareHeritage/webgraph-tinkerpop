package org.webgraph.tinkerpop.query;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.Vertex;

public class Gremlin {

    /**
     * Finds all leaves of a graph (vertices with no outgoing edges).
     *
     * @implNote uses DFS to traverse the graph, keeps visited vertices in a {@code HashSet}.
     */
    public static GraphTraversal<Vertex, Vertex> dfs(GraphTraversalSource g) {
        return g.V().not(__.in())
                .repeat(__.out().dedup());
    }

}
