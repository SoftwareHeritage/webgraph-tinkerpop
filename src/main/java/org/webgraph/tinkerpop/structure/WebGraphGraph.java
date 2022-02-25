package org.webgraph.tinkerpop.structure;

import it.unimi.dsi.big.webgraph.ImmutableGraph;
import it.unimi.dsi.big.webgraph.LazyLongIterator;
import it.unimi.dsi.big.webgraph.LazyLongIterators;
import it.unimi.dsi.big.webgraph.NodeIterator;
import it.unimi.dsi.fastutil.longs.LongLongPair;
import org.apache.commons.configuration2.BaseConfiguration;
import org.apache.commons.configuration2.Configuration;
import org.apache.tinkerpop.gremlin.process.computer.GraphComputer;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Transaction;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.util.wrapped.WrappedGraph;
import org.apache.tinkerpop.gremlin.util.iterator.IteratorUtils;
import org.webgraph.tinkerpop.graph.BidirectionalImmutableGraph;
import org.webgraph.tinkerpop.structure.provider.DefaultWebGraphPropertyProvider;
import org.webgraph.tinkerpop.structure.provider.WebGraphPropertyProvider;

import java.io.IOException;
import java.util.Iterator;

public class WebGraphGraph implements Graph, WrappedGraph<ImmutableGraph> {
    private static final String GRAPH_PATH = "webgraph.path";

    private final BidirectionalImmutableGraph graph;
    private final Configuration configuration;
    private final WebGraphPropertyProvider propertyProvider;

    private WebGraphGraph(String path, Configuration configuration) throws IOException {
        this(new BidirectionalImmutableGraph(ImmutableGraph.load(path), ImmutableGraph.load(path + "-transposed")),
                new DefaultWebGraphPropertyProvider(), configuration);
    }

    private WebGraphGraph(BidirectionalImmutableGraph graph, WebGraphPropertyProvider propertyProvider, Configuration configuration) {
        this.configuration = configuration;
        this.graph = graph;
        this.propertyProvider = propertyProvider;
    }

    @Override
    public Iterator<Vertex> vertices(Object... vertexIds) {
        if (vertexIds.length == 0) {
            return IteratorUtils.map(graph.nodeIterator(), id -> new WebGraphVertex(id, this));
        }
        return new Iterator<>() {
            int nextIndex = 0;

            @Override
            public boolean hasNext() {
                return nextIndex < vertexIds.length;
            }

            @Override
            public Vertex next() {
                Object id = vertexIds[nextIndex];
                if (!(id instanceof Number)) {
                    throw new IllegalArgumentException("Expected id of numeric type.");
                }
                nextIndex++;
                return new WebGraphVertex(((Number) id).longValue(), WebGraphGraph.this);
            }
        };
    }

    public static WebGraphGraph open(BidirectionalImmutableGraph graph, WebGraphPropertyProvider propertyProvider, String path) {
        Configuration config = EMPTY_CONFIGURATION();
        config.setProperty(GRAPH_PATH, path);
        return new WebGraphGraph(graph, propertyProvider, config);
    }

    public static Configuration EMPTY_CONFIGURATION() {
        return new BaseConfiguration() {{
            this.setProperty(Graph.GRAPH, WebGraphGraph.class.getName());
        }};
    }

    public static WebGraphGraph open(Configuration configuration) throws IOException {
        String path = configuration.getString(GRAPH_PATH);
        return new WebGraphGraph(path, configuration);
    }

    public static WebGraphGraph open(String path) throws IOException {
        Configuration config = EMPTY_CONFIGURATION();
        config.setProperty(GRAPH_PATH, path);
        return open(config);
    }

    @Override
    public Vertex addVertex(Object... keyValues) {
        throw Graph.Exceptions.vertexAdditionsNotSupported();
    }

    @Override
    public <C extends GraphComputer> C compute(Class<C> graphComputerClass) throws IllegalArgumentException {
        throw Graph.Exceptions.graphComputerNotSupported();
    }

    @Override
    public GraphComputer compute() throws IllegalArgumentException {
        throw Graph.Exceptions.graphComputerNotSupported();
    }

    @Override
    public Transaction tx() {
        throw Graph.Exceptions.transactionsNotSupported();
    }

    @Override
    public void close() {
    }

    @Override
    public Variables variables() {
        throw Graph.Exceptions.variablesNotSupported();
    }

    @Override
    public Configuration configuration() {
        return configuration;
    }

    @Override
    public Iterator<Edge> edges(Object... edgeIds) {
        if (edgeIds.length == 0) { // returns all graph edges
            // Iterate over all 'froms' and return an edge for each neighbour.
            return new Iterator<>() {
                NodeIterator froms;
                long nextFrom;
                LazyLongIterator tos;
                long nextTo;

                WebGraphEdge nextEdge = nextEdge();

                private WebGraphEdge nextEdge() {
                    if (froms == null) { // first run, initialize fields
                        froms = graph.nodeIterator();
                        nextFrom = nextFrom();
                        tos = nextFrom == -1 ? LazyLongIterators.EMPTY_ITERATOR : graph.successors(nextFrom);
                    }
                    nextTo = tos.nextLong();
                    while (nextTo == -1) { // no more successors for this node
                        nextFrom = nextFrom();
                        if (nextFrom == -1) { // no more 'from' nodes
                            return null;
                        }
                        tos = graph.successors(nextFrom);
                        nextTo = tos.nextLong();
                    }
                    return new WebGraphEdge(nextFrom, nextTo, WebGraphGraph.this);
                }

                @Override
                public boolean hasNext() {
                    return nextEdge != null;
                }

                @Override
                public Edge next() {
                    WebGraphEdge next = nextEdge;
                    nextEdge = nextEdge();
                    return next;
                }

                private long nextFrom() {
                    return froms.hasNext() ? froms.nextLong() : -1;
                }
            };
        }
        return new Iterator<>() {
            int nextIndex = 0;

            @Override
            public boolean hasNext() {
                return nextIndex < edgeIds.length;
            }

            @Override
            public Edge next() {
                Object idObj = edgeIds[nextIndex];
                if (!(idObj instanceof LongLongPair)) {
                    throw new IllegalArgumentException("Expected id of type LongLongPair.");
                }
                LongLongPair id = (LongLongPair) idObj;
                nextIndex++;
                return new WebGraphEdge(id.firstLong(), id.secondLong(), WebGraphGraph.this);
            }
        };
    }

    @Override
    public BidirectionalImmutableGraph getBaseGraph() {
        return graph;
    }

    public WebGraphPropertyProvider getPropertyProvider() {
        return propertyProvider;
    }
}
