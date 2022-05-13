package org.webgraph.tinkerpop.query;

import it.unimi.dsi.big.webgraph.BidirectionalImmutableGraph;
import org.apache.tinkerpop.gremlin.process.traversal.P;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.webgraph.tinkerpop.GremlinQueryExecutor;
import org.webgraph.tinkerpop.structure.WebGraphGraph;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;
import java.util.function.Consumer;
import java.util.function.Function;

public class DFS {
    private static final long BYTE_TO_MB_CONVERSION_VALUE = 1024 * 1024;

    private static void time(Consumer<BidirectionalImmutableGraph> query, BidirectionalImmutableGraph g) {
        time(() -> query.accept(g));
    }

    public static Function<GraphTraversalSource, GraphTraversal<Vertex, Vertex>> gremlin() {
        return g -> g.withSideEffect("a", new HashSet<>())
                     .V().not(__.in())
                     .repeat(__.out().dedup().where(P.without("a")).aggregate("a"))
                     .until(__.not(__.out()));
    }

    private static void time(Runnable r) {
        Instant start = Instant.now();
        r.run();
        System.out.println("Finished in: " + Duration.between(start, Instant.now()).toMillis() + "ms");
    }

    public static void main(String[] args) throws IOException {
        String USAGE = "Usage: DFS <graphPath> <native|native-set|gremlin>";
        if (args == null || args.length != 2) {
            System.out.println(USAGE);
            return;
        }

        WebGraphGraph graph = WebGraphGraph.open(args[0]);
        System.out.println("Nodes: " + graph.getBaseGraph().numNodes());
        System.out.println("Nodes: " + graph.getBaseGraph().numArcs());
        System.out.println("Memory after graph opened: " + getHeapMemoryUsage() + " MB");
        if (args[1].equals("native")) {
            time(Native::dfs, graph.getBaseGraph());
        } else if (args[1].equals("native-set")) {
            time(Native::dfsSet, graph.getBaseGraph());
        } else if (args[1].equals("gremlin")) {
            GremlinQueryExecutor e = new GremlinQueryExecutor(graph);
            System.out.println(e.profile(Gremlin::dfs));
        } else {
            System.out.println(USAGE);
        }
        System.out.println("Memory: " + getHeapMemoryUsage() + " MB");
    }

    public static long getHeapMemoryUsage() {
        return ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed() / BYTE_TO_MB_CONVERSION_VALUE;
    }
}
