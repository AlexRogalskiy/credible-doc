package com.credibledoc.combiner;

import com.credibledoc.combiner.context.Context;
import com.credibledoc.combiner.log.buffered.LogBufferedReader;
import com.credibledoc.combiner.log.reader.ReaderService;
import com.credibledoc.combiner.node.file.NodeFileService;
import com.credibledoc.combiner.state.FilesMergerState;
import com.credibledoc.combiner.tactic.Tactic;
import com.credibledoc.combiner.tactic.TacticService;

import java.io.File;
import java.util.List;
import java.util.Set;

/**
 * Stateful instance with {@link Context} and methods for reading lines from parsed files.
 */
public class SourceFilesReader {
    
    private Set<File> files;
    
    private FilesMergerState filesMergerState;
    
    private LogBufferedReader logBufferedReader;
    
    private Context context;

    public void addSourceFiles(Set<File> sourceFiles) {
        this.files = sourceFiles;
    }

    public List<String> read() {
        if (filesMergerState == null) {
            TacticService.getInstance().prepareReaders(files, context);
            
            filesMergerState = new FilesMergerState();
            filesMergerState.setNodeFiles(context.getNodeFileRepository().getNodeFiles());
        }
        String line = ReaderService.getInstance().readLineFromReaders(filesMergerState, context);
        logBufferedReader = ReaderService.getInstance().getCurrentReader(filesMergerState);
        if (line == null) {
            return null;
        }
        if (logBufferedReader.isNotClosed()) {
            return ReaderService.getInstance().readMultiline(line, logBufferedReader, context);
        }
        return null;
    }

    public File currentFile(Context context) {
        return NodeFileService.getInstance().findNodeFile(logBufferedReader, context).getFile();
    }

    public Tactic currentTactic(Context context) {
        return TacticService.getInstance().findTactic(logBufferedReader, context);
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
