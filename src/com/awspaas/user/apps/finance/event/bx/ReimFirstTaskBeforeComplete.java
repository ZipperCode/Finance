package com.awspaas.user.apps.finance.event.bx;

import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.InterruptListener;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.sdk.local.SDK;

public class ReimFirstTaskBeforeComplete extends InterruptListener {
    @Override
    public boolean execute(ProcessExecutionContext processExecutionContext) throws Exception {
        String bindId = processExecutionContext.getProcessInstance().getId();
        DBSql.update("UPDATE BO_EU_BX SET STATUS ='1' WHERE BINDID = '"+bindId+"'");
        return true;
    }

    public String getDescription() {
        return "报销流程:将流程的审批状态改为待审";
    }

}
