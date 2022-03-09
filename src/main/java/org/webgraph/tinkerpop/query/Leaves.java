package org.webgraph.tinkerpop.query;

import it.unimi.dsi.big.webgraph.LazyLongIterator;
import it.unimi.dsi.big.webgraph.NodeIterator;
import org.apache.tinkerpop.gremlin.process.traversal.P;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.webgraph.tinkerpop.WebgraphGremlinQueryExecutor;
import org.webgraph.tinkerpop.graph.BidirectionalImmutableGraph;
import org.webgraph.tinkerpop.structure.WebGraphGraph;
import org.webgraph.tinkerpop.structure.WebGraphVertex;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

public class Leaves {
    private static final String PYTHON_3K = "src/main/resources/python3kcompress/graph";

    public static void main(String[] args) throws IOException {
        String USAGE = "Usage: Leaves <native|native-set|gremlin>";
        if (args == null || args.length != 1) {
            System.out.println(USAGE);
            return;
        }

        WebGraphGraph graph = WebGraphGraph.open(PYTHON_3K);
        if (args[0].equals("native")) {
            leavesNative(graph);
        } else if (args[0].equals("native-set")) {
            leavesNativeSet(graph);
        } else if (args[0].equals("gremlin")) {
            leavesGremlin(graph);
        } else {
            System.out.println(USAGE);
        }
    }

    public static void leavesNative(WebGraphGraph graph) {
        time(() -> System.out.println(runLeavesNative(graph.getBaseGraph()) + " leaves"));
    }

    public static void leavesNativeSet(WebGraphGraph graph) {
        time(() -> System.out.println(runLeavesNativeSet(graph) + " leaves"));
    }

    public static void leavesGremlin(WebGraphGraph graph) {
        WebgraphGremlinQueryExecutor e = new WebgraphGremlinQueryExecutor(graph);
        e.time(leaves(), 17341338);
    }

    public static int runLeavesNative(BidirectionalImmutableGraph g) {
        NodeIterator nodes = g.nodeIterator();
        int a = 0;
        Set<Long> roots = new HashSet<>();
        while (nodes.hasNext()) {
            long cur = nodes.nextLong();
            if (g.predecessors(cur).nextLong() == -1) {
                roots.add(cur);
            }
        }
        boolean[] used = new boolean[50_000_000];
        for (Long root : roots) {
            a += dfs(root, used, g);
        }
        return a;
    }

    private static int dfs(long root, boolean[] used, BidirectionalImmutableGraph g) {
        used[(int) root] = true;
        LazyLongIterator successors = g.successors(root);
        int subLeafs = 0;
        long child;
        boolean isLeaf = true;
        while ((child = successors.nextLong()) != -1) {
            isLeaf = false;
            if (!used[(int) child]) {
                subLeafs += dfs(child, used, g);
            }
        }
        if (isLeaf) {
            return 1;
        }
        return subLeafs;
    }

    public static int runLeavesNativeSet(WebGraphGraph wg) {
        BidirectionalImmutableGraph g = wg.getBaseGraph();
        NodeIterator nodes = g.nodeIterator();
        int a = 0;
        Set<WebGraphVertex> roots = new HashSet<>();
        while (nodes.hasNext()) {
            long cur = nodes.nextLong();
            if (g.predecessors(cur).nextLong() == -1) {
                roots.add(new WebGraphVertex(cur, wg));
            }
        }
        Set<WebGraphVertex> used = new HashSet<>();
        for (WebGraphVertex root : roots) {
            a += dfsSet(root, used, wg);
        }
        return a;
    }

    private static int dfsSet(WebGraphVertex root, Set<WebGraphVertex> used, WebGraphGraph wg) {
        BidirectionalImmutableGraph g = wg.getBaseGraph();
        used.add(root);
        LazyLongIterator successors = g.successors((long) root.id());
        int subLeafs = 0;
        long child;
        boolean isLeaf = true;
        while ((child = successors.nextLong()) != -1) {
            isLeaf = false;
            WebGraphVertex childVertex = new WebGraphVertex(child, wg);
            if (!used.contains(childVertex)) {
                subLeafs += dfsSet(childVertex, used, wg);
            }
        }
        if (isLeaf) {
            return 1;
        }
        return subLeafs;
    }

    public static void time(Runnable r) {
        Instant start = Instant.now();
        r.run();
        System.out.println("Finished in: " + Duration.between(start, Instant.now()).toSeconds() + "s");
    }

    public static Function<GraphTraversalSource, GraphTraversal<Vertex, Vertex>> leaves() {
        return g -> g.withSideEffect("a", new HashSet<>())
                     .V().not(__.in())
                     .repeat(__.out().dedup().where(P.without("a")).aggregate("a"))
                     .until(__.not(__.out()));
    }
}
