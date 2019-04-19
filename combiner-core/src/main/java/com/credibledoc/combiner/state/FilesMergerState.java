package com.credibledoc.combiner.state;

import com.credibledoc.combiner.application.Application;
import com.credibledoc.combiner.node.file.NodeFile;
import com.credibledoc.combiner.node.log.NodeLog;

import java.util.ArrayList;
import java.util.List;

/**
 * Stateful object. Contains state of loaded {@link NodeFile}s
 * and a {@link #lastUsedNodeLogIndex}.
 *
 * @author Kyrylo Semenko
 */
public class FilesMergerState {

    /**
     * Contains an order number of the last used
     * {@link NodeLog#getLogBufferedReader()}
     */
    private int lastUsedNodeLogIndex;

    /**
     * Log files generated by one {@link Application}.
     */
    private List<NodeFile> nodeFiles = new ArrayList<>();

    /**
     * @return The {@link #lastUsedNodeLogIndex} field value.
     */
    public int getLastUsedNodeLogIndex() {
        return lastUsedNodeLogIndex;
    }

    /**
     * @param lastUsedNodeLogIndex see the {@link #lastUsedNodeLogIndex} field description.
     */
    public void setLastUsedNodeLogIndex(int lastUsedNodeLogIndex) {
        this.lastUsedNodeLogIndex = lastUsedNodeLogIndex;
    }

    /**
     * @return The {@link #nodeFiles} field value.
     */
    public List<NodeFile> getNodeFiles() {
        return nodeFiles;
    }

    /**
     * @param nodeFiles see the {@link #nodeFiles} field description.
     */
    public void setNodeFiles(List<NodeFile> nodeFiles) {
        this.nodeFiles = nodeFiles;
    }
}
