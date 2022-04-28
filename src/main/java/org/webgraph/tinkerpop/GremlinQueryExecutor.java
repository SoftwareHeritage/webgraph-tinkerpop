package org.webgraph.tinkerpop;

import org.apache.tinkerpop.gremlin.groovy.engine.GremlinExecutor;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.util.TraversalMetrics;
import org.apache.tinkerpop.gremlin.structure.Graph;

import javax.script.Bindings;
import javax.script.SimpleBindings;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

/**
 * This class provides utility methods that can execute {@code Gremlin} queries on the provided {@code WebGraphGraph}.
 */
public class GremlinQueryExecutor {

    private final Graph g;

    public GremlinQueryExecutor(Graph g) {
        this.g = g;
    }

    /**
     * Evaluates the given {@code Gremlin} query.
     *
     * @param query the {@code Gremlin } query
     * @return a lazy {@code GraphTraversal} which can be printed or profiled
     */
    public GraphTraversal<?, ?> eval(String query) {
        Bindings bindings = new SimpleBindings();
        bindings.put("g", g.traversal());
        try (GremlinExecutor ge = GremlinExecutor.build()
                                                 .globalBindings(bindings)
                                                 .create()) {
            return (GraphTraversal<?, ?>) ge.eval(query, bindings).get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Evaluates the given {@code Gremlin} traversal.
     *
     * @param t the {@code Gremlin } query
     * @return a lazy {@code GraphTraversal} which can be printed or profiled
     */
    public <S, E> GraphTraversal<S, E> eval(Function<GraphTraversalSource, GraphTraversal<S, E>> t) {
        return t.apply(g.traversal());
    }

    /**
     * Prints the result entities of the traversal to standard output.
     *
     * @param t the traversal
     */
    public <S, E> void print(Function<GraphTraversalSource, GraphTraversal<S, E>> t) {
        print(eval(t));
    }

    /**
     * Prints the result entities of the traversal to standard output.
     *
     * @param query the traversal query
     */
    public void print(String query) {
        print(eval(query));
    }

    /**
     * Prints the result entities of the traversal to standard output.
     *
     * @param t the traversal
     */
    public void print(GraphTraversal<?, ?> t) {
        while (t.hasNext()) {
            System.out.println(t.next());
        }
    }

    /**
     * Returns the result entities of the traversal in a List.
     *
     * @param t the traversal
     */
    public <S, E> List<E> get(Function<GraphTraversalSource, GraphTraversal<S, E>> t) {
        return get(eval(t));
    }

    /**
     * Returns the result entities of the traversal in a List.
     *
     * @param t the traversal results
     */
    public <S, E> List<E> get(GraphTraversal<S, E> t) {
        List<E> res = new ArrayList<>();
        while (t.hasNext()) {
            res.add(t.next());
        }
        return res;
    }

    /**
     * Executes the query and prints stats after the delay passes.
     *
     * @param t   the traversal
     * @param max expected limit of output entities
     */
    public <S, E> void time(Function<GraphTraversalSource, GraphTraversal<S, E>> t, long max) {
        time(eval(t), max);
    }

    /**
     * Executes the query and prints stats after the delay passes.
     *
     * @param query the traversal query
     * @param max   expected limit of output entities
     */
    public void time(String query, long max) {
        time(eval(query), max);
    }

    /**
     * Executes the query and prints stats after the delay passes.
     *
     * @param t   the traversal
     * @param max expected limit of output entities
     */
    public void time(GraphTraversal<?, ?> t, long max) {
        long delay = 1000 * 10; // 10s
        long cnt = 0;
        long start = System.currentTimeMillis();
        long last = start;
        while (t.hasNext()) {
            t.next();
            cnt++;
            long cur = System.currentTimeMillis();
            if (cur - last >= delay) {
                double epms = 1.0 * cnt / (cur - start);
                System.out.printf("%tT%n" +
                                "\tDone: %.2f%% (%d/%d)%n" +
                                "\tElapsed: %s%n" +
                                "\tEPM: %.0f%n" +
                                "\tETA: %s%n%n",
                        new Date(),
                        100.0 * cnt / max, cnt, max,
                        formatDuration(cur - start),
                        epms * 1000 * 60,
                        epms > 0 ? formatDuration((long) ((max - cnt) / epms)) : "∞");
                last = cur;
            }
        }
        last = System.currentTimeMillis();
        System.out.printf("%nTotal: %s%n", formatDuration(last - start));
    }

    /**
     * Formats provided duration in milliseconds to 'XX h YY m ZZ s'
     *
     * @param millis the duration in milliseconds
     * @return formatted duration
     */
    private String formatDuration(long millis) {
        return Duration.ofMillis(millis).truncatedTo(ChronoUnit.SECONDS).toString()
                       .substring(2)
                       .replaceAll("(\\d[HMS])(?!$)", "$1 ")
                       .toLowerCase();
    }

    /**
     * Prints the profile stats of given traversal to standard output.
     *
     * @param t the traversal to profile
     * @return the execution time of the traversal in milliseconds
     */
    public <S, E> TraversalMetrics profile(Function<GraphTraversalSource, GraphTraversal<S, E>> t) {
        return profile(eval(t));
    }

    /**
     * Prints the profile stats of given traversal to standard output.
     *
     * @param query the query to profile
     * @return the execution time of the traversal in milliseconds
     */
    public TraversalMetrics profile(String query) {
        return profile(eval(query));
    }

    /**
     * Prints the profile stats of given traversal to standard output.
     *
     * @param t the traversal to profile
     * @return the execution time of the traversal in milliseconds
     */
    public TraversalMetrics profile(GraphTraversal<?, ?> t) {
        return t.profile().next();
    }
}

