package org.webgraph.tinkerpop.structure.property.edge;

import it.unimi.dsi.big.webgraph.labelling.ArcLabelledImmutableGraph;

/**
 * Edge property getter based on {@link ArcLabelledImmutableGraph}.
 * <p>
 * Allows access to a single label for each edge. In order to deconstruct the label object, see {@link ArcLabelEdgeSubProperty}.
 */
public class ArcLabelEdgeProperty<T> extends EdgeProperty<T> {

    private static final String KEY = "__arc_label_property__";

    /**
     * Constructs an edge property from an {@code ArcLabelledImmutableGraph}.
     * The labels are used as a property with a predefined key {@link #KEY}.
     *
     * @param graph the {@code ArcLabelledImmutableGraph}.
     */
    public ArcLabelEdgeProperty(ArcLabelledImmutableGraph graph) {
        super(KEY, getArcLabelPropertyGetter(graph));
    }

    public static <T> EdgePropertyGetter<T> getArcLabelPropertyGetter(ArcLabelledImmutableGraph graph) {
        return (fromId, toId) -> {
            var s = graph.successors(fromId);
            long succ;
            while ((succ = s.nextLong()) != -1) {
                if (succ == toId) {
                    return (T) s.label().get();
                }
            }
            return null;
        };
    }
}
