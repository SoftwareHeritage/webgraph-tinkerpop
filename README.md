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

### Properties and labels

In order for Gremlin to access your properties, you need to provide an implementation
of [WebGraphPropertyProvider](https://github.com/andrey-star/webgraph-tinkerpop/blob/master/src/main/java/org/webgraph/tinkerpop/structure/provider/WebGraphPropertyProvider.java)
. The implementation will let Gremlin access node/edge properties by id.

### Example ([swh-graph](https://docs.softwareheritage.org/devel/swh-graph/))

`Server.java`

```java
public class Server {
    public Server(String graphPath) throws IOException {
        this.graphPath = graphPath;
        this.graph = Graph.loadLabelled(graphPath); // Load graph into memory. Labels will provide edge properties.
        this.graph.loadAuthorTimestamp(); // Load a property file.
        
        this.propertyProvider = new SwhWebGraphPropertyProvider(graph);
    }

    public void printQuery(String query) {
        try (WebGraphGraph g = WebGraphGraph.open(graph.getGraph(), graphSettings, graphPath)) {
            WebgraphGremlinQueryExecutor e = new WebgraphGremlinQueryExecutor(g);
            e.print(query);
        }
    }
}
```

`SwhWebGraphPropertyProvider.java`

```java
public class SwhWebGraphPropertyProvider implements WebGraphPropertyProvider {
    private final Graph graph;

    public SwhWebGraphPropertyProvider(Graph graph) {
        this.graph = graph;
    }

    @Override
    public String nodeLabel(long nodeId) {
        return graph.getNodeType(nodeId).toString();
    }

    @Override
    public String[] nodeProperties(long nodeId) {
        return new String[]{"author_timestamp"};
    }

    @Override
    public Object nodeProperty(String key, long nodeId) {
        if (!"author_timestamp".equals(key)) {
            throw new RuntimeException("Unknown property key: " + key);
        }
        long authorTimestamp = graph.getAuthorTimestamp(nodeId);
        return authorTimestamp == Long.MIN_VALUE ? null : authorTimestamp;
    }

    @Override
    public String[] arcProperties(long fromId, long toId) {
        return new String[]{"dir_entry"};
    }

    @Override
    public String arcLabel(long fromId, long toId) {
        return "edge";
    }

    @Override
    public Object arcProperty(String key, long fromId, long toId) {
        if (!key.equals("dir_entry")) {
            throw new RuntimeException("Unknown property key: " + key);
        }
        var s = graph.labelledSuccessors(fromId);
        long succ;
        while ((succ = s.nextLong()) != -1) {
            if (succ == toId) {
                return s.label().get();
            }
        }
        return null;
    }
}
```
