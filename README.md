# [TinkerPop](https://tinkerpop.apache.org/) implementation for [WebGraph](https://webgraph.di.unimi.it/)
Provides the ability to execute Gremlin queries on graphs compressed with WebGraph.
________
## Build

```shell
mvn compile assembly:single
```
________________
## Execute query (Java 11)

```shell
java -cp target/webgraph-tinkerpop-*.jar org.webgraph.tinkerpop.Main <graph_path> <query> [--profile]
```

if specified `--profile` will output query metrics instead of the results.

### Example

Print vertices

```shell
java -cp target/webgraph-tinkerpop-*.jar org.webgraph.tinkerpop.Main "src/main/resources/example/example" "g.V()" 
```

___

## Usage

Open
a [WebGraphGraph](https://github.com/andrey-star/webgraph-tinkerpop/blob/master/src/main/java/org/webgraph/tinkerpop/structure/WebGraphGraph.java)
instance with one of the `open` methods.
Use [WebgraphGremlinQueryExecutor](https://github.com/andrey-star/webgraph-tinkerpop/blob/master/src/main/java/org/webgraph/tinkerpop/WebgraphGremlinQueryExecutor.java)
to execute `Gremlin` queries.

To override default vertex/edge labels implement
the [WebGraphSettings](https://github.com/andrey-star/webgraph-tinkerpop/blob/master/src/main/java/org/webgraph/tinkerpop/structure/settings/WebGraphSettings.java)
interface.

### Example ([swh-graph](https://docs.softwareheritage.org/devel/swh-graph/))

`Server.java`

```java
public Server(String graphPath) throws IOException {
        this.graph = Graph.loadMapped(graphPath);
        this.graphSettings = new SwhWebGraphSettings(graph);
}

public void printQuery(String query) {
    try (WebGraphGraph g = WebGraphGraph.open(
            graph.getGraph().getForwardGraph(),
            graph.getGraph().getBackwardGraph(),
            graphPath, graphSettings)) {
        WebgraphGremlinQueryExecutor e = new WebgraphGremlinQueryExecutor(g);
        e.print(query);
    }
}
```

`SwhWebGraphSettings.java`

```java
public class SwhWebGraphSettings extends DefaultWebGraphSettings {
    private final Graph graph;

    public SwhWebGraphSettings(Graph graph) {
        this.graph = graph;
    }

    @Override
    public String vertexLabel(long vertexId) {
        return graph.getNodeType(vertexId).toString();
    }
}
```
