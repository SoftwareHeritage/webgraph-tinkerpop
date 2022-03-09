package org.webgraph.tinkerpop.graph;

import it.unimi.dsi.big.webgraph.ImmutableGraph;
import it.unimi.dsi.big.webgraph.LazyLongIterator;
import it.unimi.dsi.big.webgraph.Transform;
import it.unimi.dsi.fastutil.longs.LongIterator;

/**
 * A directed immutable graph which can be iterated in both directions (forward and backward). It
 * exposes the backward equivalents of the ImmutableGraph primitives (indegree() and
 * predecessors()). This is implemented by passing two graphs, one in the forward and one in the
 * backward direction.
 */
public class BidirectionalImmutableGraph extends ImmutableGraph {
    private final ImmutableGraph forwardGraph;
    private final ImmutableGraph backwardGraph;

    /**
     * Creates a bidirectional immutable graph
     *
     * @param forwardGraph  The kgraph in the forward direction
     * @param backwardGraph The graph in the backward direction
     */
    public BidirectionalImmutableGraph(ImmutableGraph forwardGraph, ImmutableGraph backwardGraph) {
        this.forwardGraph = forwardGraph;
        this.backwardGraph = backwardGraph;
    }

    @Override
    public long numNodes() {
        assert forwardGraph.numNodes() == backwardGraph.numNodes();
        return this.forwardGraph.numNodes();
    }

    @Override
    public long numArcs() {
        assert forwardGraph.numArcs() == backwardGraph.numArcs();
        return this.forwardGraph.numArcs();
    }

    @Override
    public boolean randomAccess() {
        return this.forwardGraph.randomAccess() && this.backwardGraph.randomAccess();
    }

    @Override
    public boolean hasCopiableIterators() {
        return forwardGraph.hasCopiableIterators() && backwardGraph.hasCopiableIterators();
    }

    @Override
    public BidirectionalImmutableGraph copy() {
        return new BidirectionalImmutableGraph(this.forwardGraph.copy(), this.backwardGraph.copy());
    }

    /**
     * Returns the transposed version of the bidirectional graph. Successors become predecessors, and
     * vice-versa.
     */
    public BidirectionalImmutableGraph transpose() {
        return new BidirectionalImmutableGraph(backwardGraph, forwardGraph);
    }

    /**
     * Returns the symmetric version of the bidirectional graph. It returns the (lazy) union of the
     * forward graph and the backward graph. This is equivalent to removing the directionality of the
     * edges: the successors of a node are also its predecessors.
     *
     * @return a symmetric, undirected BidirectionalImmutableGraph.
     */
    public BidirectionalImmutableGraph symmetrize() {
        ImmutableGraph symmetric = Transform.union(forwardGraph, backwardGraph);
        return new BidirectionalImmutableGraph(symmetric, symmetric);
    }

    /**
     * Returns the simplified version of the bidirectional graph. Works like symmetrize(), but also
     * removes the loop edges.
     *
     * @return a simplified (loopless and symmetric) BidirectionalImmutableGraph
     */
    public BidirectionalImmutableGraph simplify() {
        ImmutableGraph simplified = Transform.simplify(forwardGraph, backwardGraph);
        return new BidirectionalImmutableGraph(simplified, simplified);
    }

    /**
     * Returns the outdegree of a node
     */
    @Override
    public long outdegree(long l) {
        return forwardGraph.outdegree(l);
    }

    /**
     * Returns the indegree of a node
     */
    public long indegree(long l) {
        return backwardGraph.outdegree(l);
    }

    /**
     * Returns a lazy iterator over the successors of a given node.
     */
    @Override
    public LazyLongIterator successors(long nodeId) {
        return forwardGraph.successors(nodeId);
    }

    /**
     * Returns a lazy iterator over the predecessors of a given node.
     */
    public LazyLongIterator predecessors(long nodeId) {
        return backwardGraph.successors(nodeId);
    }

    /**
     * Returns a reference to an array containing the predecessors of a given node.
     */
    public long[][] predecessorBigArray(long x) {
        return backwardGraph.successorBigArray(x);
    }

    /**
     * Returns an iterator enumerating the indegrees of the nodes of this graph.
     */
    public LongIterator indegrees() {
        return backwardGraph.outdegrees();
    }

    /**
     * Returns the underlying ImmutableGraph in the forward direction.
     */
    public ImmutableGraph getForwardGraph() {
        return forwardGraph;
    }

    /**
     * Returns the underlying ImmutableGraph in the backward direction.
     */
    public ImmutableGraph getBackwardGraph() {
        return backwardGraph;
    }
}