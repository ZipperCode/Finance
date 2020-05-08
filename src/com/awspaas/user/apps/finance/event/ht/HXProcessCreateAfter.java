package com.awspaas.user.apps.finance.event.ht;

import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.ExecuteListener;
import com.actionsoft.bpms.util.DBSql;

public class HXProcessCreateAfter extends ExecuteListener {
    @Override
    public String getDescription() {
        return "合同流程创建后更细Status变量为0";
    }

    @Override
    public void execute(ProcessExecutionContext processExecutionContext) throws Exception {
        String bindId = processExecutionContext.getProcessInstance().getId();
        DBSql.update("UPDATE BO_EU_LC_CONTRACTTURNKEY SET STATUS = '0' WHERE BINDID=?",new Object[]{bindId});
    }
}
