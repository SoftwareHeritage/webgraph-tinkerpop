package org.webgraph.tinkerpop.structure.property.edge;

import it.unimi.dsi.big.webgraph.labelling.ArcLabelledImmutableGraph;

/**
 * Edge property getter based on {@link ArcLabelEdgeProperty}.
 * <p>
 * Allows deconstructing the single WebGraph edge label into separate properties.
 * <pre>
 * {@code
 * ArcLabelEdgeProperty<DirEntry> arcLabelProperty = new ArcLabelEdgeProperty<>(arcLabelledGraph);
 * ArcLabelEdgeSubProperty subProperty = new ArcLabelEdgeSubProperty<>(
 *      "sub_property", arcLabelProperty, entry -> entry.field)
 * provider.addEdgeProperty(subProperty);
 * }
 * </pre>
 */
public class ArcLabelEdgeSubProperty<T> extends EdgeProperty<T> {

    /**
     * Constructs an edge property from the WebGraph label property.
     *
     * @param key              the string key of the property
     * @param arcLabelProperty the base {@link ArcLabelEdgeProperty} which retrieves the label from {@link ArcLabelledImmutableGraph}.
     * @param getter           a function which retrieves the sub property from the label object.
     */
    public <E> ArcLabelEdgeSubProperty(String key, ArcLabelEdgeProperty<E> arcLabelProperty, ArcLabelEdgeSubPropertyGetter<E, T> getter) {
        super(key, getArcLabelPropertyGetter(arcLabelProperty, getter));
    }

    public static <E, T> EdgePropertyGetter<T> getArcLabelPropertyGetter(ArcLabelEdgeProperty<E> arcLabelProperty, ArcLabelEdgeSubPropertyGetter<E, T> getter) {
        return (fromId, toId) -> {
            E value = arcLabelProperty.get(fromId, toId);
            return getter.get(value);
        };
    }

}
