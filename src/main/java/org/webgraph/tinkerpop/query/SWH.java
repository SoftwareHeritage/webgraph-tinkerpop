package org.webgraph.tinkerpop.query;

import org.apache.tinkerpop.gremlin.process.traversal.Order;
import org.apache.tinkerpop.gremlin.process.traversal.P;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import java.util.HashSet;
import java.util.function.Function;

public class SWH {
    /**
     * Finds all leaves (vertices with no outgoing edges) in a subtree rooted at the provided vertex.
     *
     * @implNote uses DFS to traverse the graph, keeps visited vertices in a {@code HashSet}.
     */
    public static Function<GraphTraversalSource, GraphTraversal<Vertex, Vertex>> leaves(long root) {
        return g -> g.withSideEffect("a", new HashSet<>())
                     .V(root)
                     .repeat(__.out().dedup().where(P.without("a")).aggregate("a"))
                     .until(__.not(__.out()));
    }

    /**
     * Finds all commits, which contain the provided dir/file.
     *
     * @param v the id of the dir/file vertex.
     * @return all containing commit vertices.
     */
    public static Function<GraphTraversalSource, GraphTraversal<Vertex, Vertex>> containingCommits(long v) {
        return g -> g.withSideEffect("a", new HashSet<>())
                     .V(v)
                     .repeat(__.in().dedup().where(P.without("a")).aggregate("a"))
                     .emit(__.hasLabel("REV"))
                     .dedup();
    }

    /**
     * Finds up to {@code limit} earliest commits, which contain the provided dir/file.
     *
     * @param v     the id of the dir/file vertex.
     * @param limit the number of commits to find.
     * @return up to {@code limit} earliest commits, containing the specified dir/file.
     */
    public static Function<GraphTraversalSource, GraphTraversal<Vertex, Vertex>> earliestContainingCommits(long v, long limit) {
        return containingCommits(v).andThen(g -> g.order().by("author_timestamp", Order.asc).limit(limit));
    }

    /**
     * Find the earliest containing commit of the provided dir/file.
     *
     * @param v the id of the dir/file vertex.
     * @return earliest containing commit vertex.
     */
    public static Function<GraphTraversalSource, GraphTraversal<Vertex, Vertex>> earliestContainingCommit(long v) {
        return earliestContainingCommits(v, 1);
    }

    /**
     * Finds an origin of the earliest containing commit of the provided dir/file.
     *
     * @param v the id of the dir/file vertex.
     * @return an origin vertex.
     */
    public static Function<GraphTraversalSource, GraphTraversal<Vertex, Vertex>> originOfEarliestContainingCommit(long v) {
        return g -> g.withSideEffect("a", new HashSet<>())
                     .withSideEffect("b", new HashSet<>())
                     .V(v)
                     .repeat(__.in().dedup().where(P.without("a")).aggregate("a"))
                     .emit(__.hasLabel("REV"))
                     .dedup()
                     .repeat(__.in().dedup().where(P.without("b")).aggregate("b"))
                     .until(__.hasLabel("ORI"));
    }

}
