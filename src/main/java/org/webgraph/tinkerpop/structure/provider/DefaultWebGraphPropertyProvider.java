package org.webgraph.tinkerpop.structure.provider;

/**
 * Provides stub labels for WebGraph edges and vertices.
 */
public class DefaultWebGraphPropertyProvider implements WebGraphPropertyProvider {

    @Override
    public String nodeLabel(long nodeId) {
        return null;
    }

    @Override
    public String[] nodeProperties(long nodeId) {
        return new String[0];
    }

    @Override
    public Object nodeProperty(String key, long nodeId) {
        return null;
    }

    @Override
    public String[] arcProperties(long fromId, long toId) {
        return new String[0];
    }

    @Override
    public String arcLabel(long fromId, long toId) {
        return null;
    }

    @Override
    public Object arcProperty(String key, long fromId, long toId) {
        return null;
    }
}
