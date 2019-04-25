package com.credibledoc.combiner.doc.module.combiner.launching;

import com.credibledoc.combiner.doc.CombinerDocMain;
import com.credibledoc.combiner.log.buffered.LogBufferedReader;
import com.credibledoc.combiner.doc.reportdocument.ReportDocument;
import com.credibledoc.combiner.doc.searchcommand.SearchCommand;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LaunchingSearchCommand implements SearchCommand {

    @Override
    public boolean isApplicable(ReportDocument reportDocument, List<String> multiLine, LogBufferedReader logBufferedReader) {
        return multiLine.get(0).contains(CombinerDocMain.APPLICATION_SUBSTITUTION_DOC_LAUNCHED);
    }
}
