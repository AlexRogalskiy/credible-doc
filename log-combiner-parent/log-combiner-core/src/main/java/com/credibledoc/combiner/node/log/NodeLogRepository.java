package com.credibledoc.combiner.node.log;

import java.util.HashSet;
import java.util.Set;

/**
 * A stateful service. Contains a list of {@link NodeLog}s.
 *
 * @author Kyrylo Semenko
 */
public class NodeLogRepository {

    /**
     * Singleton.
     */
    private static NodeLogRepository instance;

    /**
     * @return The {@link NodeLogRepository} singleton.
     */
    public static NodeLogRepository getInstance() {
        if (instance == null) {
            instance = new NodeLogRepository();
        }
        return instance;
    }

    /**
     * Files generated by some application.
     */
    private Set<NodeLog> nodeLogs = new HashSet<>();

    /**
     * @return the {@link #nodeLogs} value
     */
    Set<NodeLog> getNodeLogs() {
        return nodeLogs;
    }

}
