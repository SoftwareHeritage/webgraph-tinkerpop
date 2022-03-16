package org.webgraph.tinkerpop.structure.property.edge;

import it.unimi.dsi.big.webgraph.labelling.ArcLabelledImmutableGraph;

public class ArcLabelEdgeProperty implements EdgeProperty {

    private final ArcLabelledImmutableGraph graph;

    public ArcLabelEdgeProperty(ArcLabelledImmutableGraph graph) {
        this.graph = graph;
    }

    @Override
    public String getKey() {
        return "__arc_label_property__";
    }

    @Override
    public Object get(long fromId, long toId) {
        var s = graph.successors(fromId);
        long succ;
        while ((succ = s.nextLong()) != -1) {
            if (succ == toId) {
                return s.label().get();
            }
        }
        return null;
    }
}
