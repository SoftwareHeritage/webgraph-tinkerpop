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
