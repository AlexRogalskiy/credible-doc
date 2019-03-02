package org.credibledoc.substitution.doc.application;

import com.credibledoc.substitution.core.exception.SubstitutionRuntimeException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.credibledoc.substitution.doc.application.identifier.ApplicationIdentifier;
import org.credibledoc.substitution.doc.log.buffered.LogBufferedReader;
import org.credibledoc.substitution.doc.module.tactic.TacticHolder;
import org.credibledoc.substitution.doc.node.applicationlog.ApplicationLog;
import org.credibledoc.substitution.doc.node.applicationlog.ApplicationLogService;
import org.credibledoc.substitution.doc.node.log.NodeLog;
import org.credibledoc.substitution.doc.node.log.NodeLogService;
import org.credibledoc.substitution.doc.specific.SpecificTactic;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;

/**
 * A service for working with {@link TacticHolder}s.
 * @author Kyrylo Semenko
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class ApplicationService {

    public static final String RIGHT_SQUARE_BRACKET = "]";
    public static final String LEFT_SQUARE_BRACKET = "[";
    public static final String PIPE = "|";

    @NonNull
    private final List<ApplicationIdentifier> applicationIdentifiers;

    @NonNull
    private final ApplicationContext applicationContext;

    @NonNull
    private final NodeLogService nodeLogService;

    @NonNull
    private final ApplicationLogService applicationLogService;

    /**
     * Recognize, which {@link TacticHolder} the line belongs to.
     * @param line the line from the log file
     * @return {@link TacticHolder} or 'null' if not found
     */
    public TacticHolder findApplication(String line, LogBufferedReader logBufferedReader) {
        for (ApplicationIdentifier applicationIdentifier : applicationIdentifiers) {
            if (applicationIdentifier.identifyApplication(line, logBufferedReader)) {
                return applicationIdentifier.getSpecificTacticHolder();
            }
        }
        return null;
    }

    /**
     * Recognize, which {@link TacticHolder} the line belongs to.
     * @param logBufferedReader links to a {@link TacticHolder}
     * @return {@link TacticHolder} or throw exception
     */
    public TacticHolder findApplication(LogBufferedReader logBufferedReader) {
        for (ApplicationLog applicationLog : applicationLogService.getApplicationLogs()) {
            for (NodeLog nodeLog : nodeLogService.findNodeLogs(applicationLog)) {
                if (nodeLog.getLogBufferedReader() == logBufferedReader) {
                    return applicationLog.getTacticHolder();
                }
            }
        }
        throw new SubstitutionRuntimeException("Cannot find out Application. LogBufferedReader: " + logBufferedReader);
    }

    /**
     * Find out {@link ApplicationLog}. Create a new one if it not exists.
     * @param applicationLogs collection of {@link ApplicationLog}s
     * @param tacticHolder search parameter
     * @return searched or created {@link ApplicationLog}
     */
    public ApplicationLog findOrCreate(List<ApplicationLog> applicationLogs, TacticHolder tacticHolder) {
        for (ApplicationLog applicationLog : applicationLogs) {
            if (tacticHolder == applicationLog.getTacticHolder()) {
                return applicationLog;
            }
        }
        ApplicationLog applicationLog = new ApplicationLog();
        applicationLogService.addApplicationLog(applicationLog);
        applicationLog.setTacticHolder(tacticHolder);
        applicationLogs.add(applicationLog);
        return applicationLog;
    }

    /**
     * Find out {@link NodeLog#getLogBufferedReader()} from report
     * that equals with the first parameter
     *
     * @param logBufferedReader for {@link NodeLog} searching
     * @return an {@link TacticHolder#getSpecificTacticClass()} instance from the
     * {@link #applicationContext}
     */
    public SpecificTactic findSpecificTactic(@NonNull LogBufferedReader logBufferedReader) {
        for (ApplicationLog applicationLog : applicationLogService.getApplicationLogs()) {
            for (NodeLog nodeLog : nodeLogService.findNodeLogs(applicationLog)) {
                if (nodeLog.getLogBufferedReader() == logBufferedReader) {
                    Class<? extends SpecificTactic> dateFinderStrategyClass
                            = applicationLog.getTacticHolder().getSpecificTacticClass();
                    return applicationContext.getBean(dateFinderStrategyClass);
                }
            }
        }
        throw new SubstitutionRuntimeException("Cannot find out SpecificTactic");
    }
}
