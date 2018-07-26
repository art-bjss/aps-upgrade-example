package scot.disclosure.activitibpm.utils;

import org.activiti.engine.ManagementService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.ExecutionQuery;
import org.activiti.engine.runtime.Job;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Component
public class TaskShortcuts {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskShortcuts.class);

    private final RuntimeService runtimeService;

    private final ManagementService managementService;

    @Autowired
    public TaskShortcuts(RuntimeService runtimeService, ManagementService managementService) {
        this.runtimeService = runtimeService;
        this.managementService = managementService;
    }

    public List<String> expireTimerByProcessDefinitionName(
        String processDefinitionName,
        String variableName,
        String variableValue
    ) {
        LOGGER.debug("Expiring timers for process '{}', where {}='{}'",
            processDefinitionName, variableName, variableValue);
        return expireTimer(
            variableName,
            variableValue,
            executionQuery -> executionQuery.processDefinitionName(processDefinitionName)
        );
    }

    public List<String> expireTimerByProcessDefinitionKey(
        String processDefinitionKey,
        String variableName,
        String variableValue
    ) {
        return expireTimer(
            variableName,
            variableValue,
            executionQuery -> executionQuery.processDefinitionKey(processDefinitionKey)
        );
    }

    private List<String> expireTimer(String variableName, String variableValue, Function<ExecutionQuery, ExecutionQuery> filterFunction) {

        List<String> processInstanceIdList = new ArrayList<>();

        ExecutionQuery executionQuery = filterFunction.apply(runtimeService.createExecutionQuery());

        executionQuery
            .variableValueEquals(variableName, variableValue)
            .list()
            .forEach(execution -> {
                final String processInstanceId = execution.getProcessInstanceId();
                Optional.ofNullable(
                    managementService.createJobQuery()
                        .timers()
                        .processInstanceId(processInstanceId)
                        .singleResult()
                )
                    .map(Job::getId)
                    .ifPresent(jobId -> {
                        LOGGER.debug("Found job ID {}; executing", jobId);
                        managementService.executeJob(jobId);
                        processInstanceIdList.add(processInstanceId);
                    });
            });

        return processInstanceIdList;
    }


}


