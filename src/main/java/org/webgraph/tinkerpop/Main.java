package org.webgraph.tinkerpop;

import org.webgraph.tinkerpop.structure.WebGraphGraph;

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
            var executor = new WebgraphGremlinQueryExecutor(g);
            if (profile) {
                executor.profile(query);
            } else {
                executor.print(query);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
