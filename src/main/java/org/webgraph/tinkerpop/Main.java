package org.webgraph.tinkerpop;

import org.apache.tinkerpop.gremlin.groovy.engine.GremlinExecutor;
import org.apache.tinkerpop.gremlin.process.traversal.Traversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.webgraph.tinkerpop.structure.WebGraphGraph;

import javax.script.Bindings;
import javax.script.SimpleBindings;

public class Main {

    public static void main(String[] args) {
        if (args == null || args.length < 2 || args[0] == null || args[1] == null) {
            System.out.println("Usage: org.webgraph.tinkerpop.Main <graph_path> <query> [--profile]");
            return;
        }
        String path = args[0];
        String query = args[1];
        boolean profile = args.length == 3 && args[2].equals("--profile");
        try (WebGraphGraph g = WebGraphGraph.open(path)) {
            System.out.println("Opened graph: " + path);
            Bindings bindings = new SimpleBindings();
            bindings.put("g", g.traversal());
            try (GremlinExecutor ge = GremlinExecutor.build().globalBindings(bindings).create()) {
                GraphTraversal<?, ?> result = (GraphTraversal<?, ?>) ge.eval(query, bindings).get();
                if (profile) {
                    print(result.profile());
                } else {
                    print(result);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static <S, E> void print(Traversal<S, E> ts) {
        StringBuilder sb = new StringBuilder("\nResult:\n");
        ts.toList().forEach(t -> sb.append(t).append("\n"));
        System.out.print(sb);
    }
}
