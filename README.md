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
. The implementation will let Gremlin access node/edge properties by id. You can either
use [SimpleWebGraphPropertyProvider](https://github.com/andrey-star/webgraph-tinkerpop/blob/master/src/main/java/org/webgraph/tinkerpop/structure/provider/SimpleWebGraphPropertyProvider.java)
, or provide your own implementation.

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
    public String vertexLabel(long nodeId) {
        return graph.getNodeType(nodeId).toString();
    }

    @Override
    public String[] vertexProperties(long nodeId) {
        return new String[]{"author_timestamp"};
    }

    @Override
    public Object vertexProperty(String key, long nodeId) {
        if (!"author_timestamp".equals(key)) {
            throw new RuntimeException("Unknown property key: " + key);
        }
        long authorTimestamp = graph.getAuthorTimestamp(nodeId);
        return authorTimestamp == Long.MIN_VALUE ? null : authorTimestamp;
    }

    @Override
    public String[] edgeProperties(long fromId, long toId) {
        return new String[]{"dir_entry"};
    }

    @Override
    public String edgeLabel(long fromId, long toId) {
        return "edge";
    }

    @Override
    public Object edgeProperty(String key, long fromId, long toId) {
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

### Example with [SimpleWebGraphPropertyProvider](https://github.com/andrey-star/webgraph-tinkerpop/blob/master/src/main/java/org/webgraph/tinkerpop/structure/provider/SimpleWebGraphPropertyProvider.java)

`Server.java`

```java
public class Server {
    public Server(String graphPath) throws IOException {
        this.graph = Graph.loadLabelled(graphPath);  // Load graph into memory. Labels will provide edge properties.
        this.propertyProvider = new SimpleWebGraphPropertyProvider();
        this.propertyProvider.addVertexProperty(new FileVertexProperty<>("author_timestamp", Long.class, Path.of(path + ".property.author_timestamp.bin"))); // FileVertexProperty will read the property value from disk
        this.propertyProvider.addEdgeProperty(new ArcLabelEdgeProperty<>((ArcLabelledImmutableGraph) graph.getGraph().getForwardGraph())); // Use arc labels as edge property.
        this.propertyProvider.setVertexLabeller(id -> graph.getNodeType(id).toString()); // Provide custom vertex labels
    }

    public void printQuery(String query) {
        try (WebGraphGraph g = WebGraphGraph.open(graph.getGraph(), graphSettings, graphPath)) {
            WebgraphGremlinQueryExecutor e = new WebgraphGremlinQueryExecutor(g);
            e.print(query);
        }
    }
}
```
