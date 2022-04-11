package org.webgraph.tinkerpop.query;

import it.unimi.dsi.big.webgraph.BidirectionalImmutableGraph;
import org.apache.tinkerpop.gremlin.process.traversal.P;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.webgraph.tinkerpop.WebgraphGremlinQueryExecutor;
import org.webgraph.tinkerpop.structure.WebGraphGraph;

import java.io.IOException;
import java.util.HashSet;
import java.util.function.Consumer;
import java.util.function.Function;

public class DFS {
    private static final String PYTHON_3K = "src/main/resources/python3kcompress/graph";
    private static final int LEAVES_COUNT = 17341338;

    public static void main(String[] args) throws IOException {
        String USAGE = "Usage: Leaves <native|native-set|gremlin>";
        if (args == null || args.length != 1) {
            System.out.println(USAGE);
            return;
        }

        WebGraphGraph graph = WebGraphGraph.open(PYTHON_3K);
        if (args[0].equals("native")) {
            time(Native::dfs, graph.getBaseGraph());
        } else if (args[0].equals("native-set")) {
            time(Native::dfsSet, graph.getBaseGraph());
        } else if (args[0].equals("gremlin")) {
            WebgraphGremlinQueryExecutor e = new WebgraphGremlinQueryExecutor(graph);
            e.time(Gremlin::leaves, LEAVES_COUNT);
        } else {
            System.out.println(USAGE);
        }
    }

    private static void time(Consumer<BidirectionalImmutableGraph> query, BidirectionalImmutableGraph g) {
        Utils.time(() -> query.accept(g));
    }

    public static Function<GraphTraversalSource, GraphTraversal<Vertex, Vertex>> gremlin() {
        return g -> g.withSideEffect("a", new HashSet<>())
                     .V().not(__.in())
                     .repeat(__.out().dedup().where(P.without("a")).aggregate("a"))
                     .until(__.not(__.out()));
    }
}
