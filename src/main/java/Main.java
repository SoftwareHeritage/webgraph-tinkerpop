import org.apache.tinkerpop.gremlin.groovy.engine.GremlinExecutor;
import org.apache.tinkerpop.gremlin.process.traversal.Traversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.webgraph.tinkerpop.structure.WebGraphGraph;

import javax.script.Bindings;
import javax.script.SimpleBindings;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class Main {

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        if (args == null || args.length != 2 || args[0] == null || args[1] == null) {
            System.out.println("Usage: Main <graph_path> <query>");
            return;
        }
        String path = args[0];
        String query = args[1];

        try (WebGraphGraph g = WebGraphGraph.open(path)) {
            Bindings bindings = new SimpleBindings();
            bindings.put("g", g.traversal());
            GremlinExecutor ge = GremlinExecutor.build().globalBindings(bindings).create();

            CompletableFuture<Object> evalResult = ge.eval(query, bindings);
            GraphTraversal<?, ?> actualResult = (GraphTraversal<?, ?>) evalResult.get();
            ge.getExecutorService().shutdown();

            print(actualResult);
        }
    }

    private static <S, E> void print(Traversal<S, E> ts) {
        System.out.println("\nResult:");
        ts.toList().forEach(System.out::println);
    }
}
