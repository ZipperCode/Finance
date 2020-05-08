package com.awspaas.user.apps.finance.event.project;

import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.ExecuteListener;
import com.actionsoft.bpms.util.DBSql;

public class ProjectProcessCreateAfter extends ExecuteListener {
    @Override
    public void execute(ProcessExecutionContext processExecutionContext) throws Exception {
        String bindId = processExecutionContext.getProcessInstance().getId();
        DBSql.update("UPDATE BO_EU_PROJECT SET STATUS = '0' WHERE BINDID = ?",new Object[]{bindId});

    }
}
