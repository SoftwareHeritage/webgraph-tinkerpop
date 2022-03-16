package org.webgraph.tinkerpop.structure.provider;

/**
 * Provides stub labels for WebGraph edges and vertices.
 */
public class DefaultWebGraphPropertyProvider implements WebGraphPropertyProvider {

    @Override
    public String vertexLabel(long vertexId) {
        return null;
    }

    @Override
    public String[] vertexProperties(long vertexId) {
        return new String[0];
    }

    @Override
    public Object vertexProperty(String key, long vertexId) {
        return null;
    }

    @Override
    public String[] edgeProperties(long fromId, long toId) {
        return new String[0];
    }

    @Override
    public String edgeLabel(long fromId, long toId) {
        return null;
    }

    @Override
    public Object edgeProperty(String key, long fromId, long toId) {
        return null;
    }
}
