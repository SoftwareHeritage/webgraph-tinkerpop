package org.webgraph.tinkerpop.structure;

import it.unimi.dsi.big.webgraph.ImmutableGraph;
import it.unimi.dsi.big.webgraph.LazyLongIterator;
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
import org.webgraph.tinkerpop.structure.settings.DefaultWebGraphSettings;
import org.webgraph.tinkerpop.structure.settings.WebGraphSettings;

import java.io.IOException;
import java.util.Iterator;
import java.util.stream.Stream;

public class WebGraphGraph implements Graph, WrappedGraph<ImmutableGraph> {
    private static final String GRAPH_PATH = "webgraph.path";

    private final ImmutableGraph graph;
    private final ImmutableGraph graphTransposed;
    private final Configuration configuration;
    private final WebGraphSettings settings;

    private WebGraphGraph(String path, Configuration configuration) throws IOException {
        this(ImmutableGraph.load(path), ImmutableGraph.load(path + "-transposed"),
                new DefaultWebGraphSettings(), configuration);
    }

    private WebGraphGraph(ImmutableGraph forward, ImmutableGraph backward,
                          WebGraphSettings settings, Configuration configuration) {
        this.configuration = configuration;
        this.graph = forward;
        this.settings = settings;
        this.graphTransposed = backward;
    }

    @Override
    public Iterator<Vertex> vertices(Object... vertexIds) {
        if (vertexIds.length == 0) {
            return IteratorUtils.map(graph.nodeIterator(), id -> new WebGraphVertex(id, this));
        }
        return Stream.of(vertexIds).map(id -> {
            if (!(id instanceof Number)) {
                throw new IllegalArgumentException();
            }
            return ((Number) id).longValue();
        }).map(id -> (Vertex) new WebGraphVertex(id, this)).iterator();
    }

    @Override
    public Iterator<Edge> edges(Object... edgeIds) {
        if (edgeIds.length == 0) {
            return IteratorUtils.stream(graph.nodeIterator())
                                .flatMap(from -> {
                                    LazyLongIterator successors = graph.successors(from);
                                    return Stream.generate(successors::nextLong)
                                                 .takeWhile(i -> i != -1)
                                                 .map(to -> (Edge) new WebGraphEdge(from, to, WebGraphGraph.this));
                                })
                                .iterator();
        }
        return Stream.of(edgeIds).map(id -> {
            if (!(id instanceof LongLongPair)) {
                throw new IllegalArgumentException();
            }
            return ((LongLongPair) id);
        }).map(id -> (Edge) new WebGraphEdge(id.firstLong(), id.secondLong(), this)).iterator();
    }

    @Override
    public ImmutableGraph getBaseGraph() {
        return graph;
    }

    public ImmutableGraph getTransposedGraph() {
        return graphTransposed;
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

    public static Configuration EMPTY_CONFIGURATION() {
        return new BaseConfiguration() {{
            this.setProperty(Graph.GRAPH, WebGraphGraph.class.getName());
        }};
    }

    public static WebGraphGraph open(String path) throws IOException {
        Configuration config = EMPTY_CONFIGURATION();
        config.setProperty(GRAPH_PATH, path);
        return open(config);
    }

    public static WebGraphGraph open(ImmutableGraph forward, ImmutableGraph backward, WebGraphSettings settings) {
        Configuration config = EMPTY_CONFIGURATION();
        return new WebGraphGraph(forward, backward, settings, config);
    }

    public static WebGraphGraph open(Configuration configuration) throws IOException {
        String path = configuration.getString(GRAPH_PATH);
        return new WebGraphGraph(path, configuration);
    }

    public WebGraphSettings getSettings() {
        return settings;
    }
}
