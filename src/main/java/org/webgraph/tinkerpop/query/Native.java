package org.webgraph.tinkerpop.query;

import it.unimi.dsi.big.webgraph.BidirectionalImmutableGraph;
import it.unimi.dsi.big.webgraph.LazyLongIterator;
import it.unimi.dsi.big.webgraph.NodeIterator;
import org.webgraph.tinkerpop.structure.WebGraphVertex;

import java.util.HashSet;
import java.util.Set;

public class Native {

    /**
     * Performs a DFS on the graph.
     */
    public static void dfs(BidirectionalImmutableGraph g) {
        NodeIterator nodes = g.nodeIterator();
        Set<Long> roots = new HashSet<>();
        while (nodes.hasNext()) {
            long cur = nodes.nextLong();
            if (g.predecessors(cur).nextLong() == -1) {
                roots.add(cur);
            }
        }
        boolean[] used = new boolean[50_000_000];
        for (Long root : roots) {
            dfs(root, used, g);
        }
    }

    public static long dfsEdges(BidirectionalImmutableGraph g) {
        boolean[] used = new boolean[50_000_000];
        return dfsEdges(331702, used, g);
    }

    /**
     * Performs a DFS on the graph, keeping visited vertices in a {@code Set},
     * instead of a boolean array.
     *
     * @see #dfs(BidirectionalImmutableGraph)
     */
    public static void dfsSet(BidirectionalImmutableGraph g) {
        NodeIterator nodes = g.nodeIterator();
        Set<WebGraphVertex> roots = new HashSet<>();
        while (nodes.hasNext()) {
            long cur = nodes.nextLong();
            if (g.predecessors(cur).nextLong() == -1) {
                roots.add(new WebGraphVertex(cur, null));
            }
        }
        Set<WebGraphVertex> used = new HashSet<>();
        for (WebGraphVertex root : roots) {
            dfsSet(root, used, g);
        }
    }

    private static void dfs(long root, boolean[] used, BidirectionalImmutableGraph g) {
        used[(int) root] = true;
        LazyLongIterator successors = g.successors(root);
        long child;
        while ((child = successors.nextLong()) != -1) {
            if (!used[(int) child]) {
                dfs(child, used, g);
            }
        }
    }

    private static void dfsSet(WebGraphVertex root, Set<WebGraphVertex> used, BidirectionalImmutableGraph g) {
        used.add(root);
        LazyLongIterator successors = g.successors((long) root.id());
        long child;
        while ((child = successors.nextLong()) != -1) {
            WebGraphVertex childVertex = new WebGraphVertex(child, null);
            if (!used.contains(childVertex)) {
                dfsSet(childVertex, used, g);
            }
        }
    }

    private static long dfsEdges(long root, boolean[] used, BidirectionalImmutableGraph g) {
        used[(int) root] = true;
        LazyLongIterator successors = g.successors(root);
        long child;
        long res = 0;
        while ((child = successors.nextLong()) != -1) {
            res += 1;
            if (!used[(int) child]) {
                res += dfsEdges(child, used, g);
            }
        }
        return res;
    }
}
