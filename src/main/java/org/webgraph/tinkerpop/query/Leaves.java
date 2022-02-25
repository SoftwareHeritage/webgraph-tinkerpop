package org.webgraph.tinkerpop.query;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import org.apache.tinkerpop.gremlin.process.traversal.P;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.T;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.webgraph.tinkerpop.WebgraphGremlinQueryExecutor;
import org.webgraph.tinkerpop.structure.WebGraphGraph;

import java.io.IOException;
import java.util.HashSet;
import java.util.function.Function;

public class Leaves {
    private static final String PYTHON_3K = "src/main/resources/python3kcompress/graph";

    public static void main(String[] args) throws IOException {
        WebGraphGraph graph = WebGraphGraph.open(PYTHON_3K);

        WebgraphGremlinQueryExecutor e = new WebgraphGremlinQueryExecutor(graph);
        e.profile(leavesDedupedByVertex());
    }

    /**
     * Returns all leaves of the graph.
     * The query finds all roots of the graph, then traverses each subtree in a DFS, keeping a pool of visited vertices.
     */
    public static Function<GraphTraversalSource, GraphTraversal<Vertex, Vertex>> leavesDedupedByVertex() {
        return g -> g.withSideEffect("a", new HashSet<>()) // to store visited vertices
                     .V().not(__.in())// find roots
                     .repeat(__.out().dedup()
                               .where(P.without("a")) // filter seen vertices
                               .aggregate("a")) // save new vertices
                     .until(__.not(__.out())); // identify leaf
    }

    /**
     * Returns all leaves of the graph.
     * The query finds all roots of the graph, then traverses each subtree in a DFS, keeping a ids of visited vertices in set.
     */
    public static Function<GraphTraversalSource, GraphTraversal<Vertex, Vertex>> leavesDedupedById() {
        return g -> g.withSideEffect("a", new LongOpenHashSet()) // to store ids of visited vertices
                     .V().not(__.in())// find roots
                     .repeat(__.out().dedup()
                               .filter(__.id().where(P.without("a"))) // filter seen vertices by id
                               .aggregate("a").by(T.id)) // save ids of new vertices
                     .until(__.not(__.out()));
    }
}
