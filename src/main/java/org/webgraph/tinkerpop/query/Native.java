package org.webgraph.tinkerpop.query;

import it.unimi.dsi.big.webgraph.BidirectionalImmutableGraph;
import it.unimi.dsi.big.webgraph.LazyLongIterator;
import it.unimi.dsi.big.webgraph.NodeIterator;
import org.webgraph.tinkerpop.structure.WebGraphGraph;
import org.webgraph.tinkerpop.structure.WebGraphVertex;
import org.webgraph.tinkerpop.structure.provider.StandardWebGraphPropertyProvider;

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
        boolean[] used = new boolean[(int) g.numNodes()];
        for (Long root : roots) {
            dfs(root, used, g);
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

    /**
     * Performs a DFS on the graph, keeping visited vertices in a {@code Set},
     * instead of a boolean array.
     *
     * @see #dfs(BidirectionalImmutableGraph)
     */
    public static void dfsSet(BidirectionalImmutableGraph g) {
        WebGraphGraph wgg = WebGraphGraph.open(null, new StandardWebGraphPropertyProvider(), null);
        NodeIterator nodes = g.nodeIterator();
        Set<WebGraphVertex> roots = new HashSet<>();
        while (nodes.hasNext()) {
            long cur = nodes.nextLong();
            if (g.predecessors(cur).nextLong() == -1) {
                roots.add(new WebGraphVertex(cur, wgg));
            }
        }
        Set<WebGraphVertex> used = new HashSet<>();
        for (WebGraphVertex root : roots) {
            dfsSet(root, used, g, wgg);
        }
    }

    private static void dfsSet(WebGraphVertex root, Set<WebGraphVertex> used, BidirectionalImmutableGraph g, WebGraphGraph wgg) {
        used.add(root);
        LazyLongIterator successors = g.successors((long) root.id());
        long child;
        while ((child = successors.nextLong()) != -1) {
            WebGraphVertex childVertex = new WebGraphVertex(child, wgg);
            if (!used.contains(childVertex)) {
                dfsSet(childVertex, used, g, wgg);
            }
        }
    }

}
