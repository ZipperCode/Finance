package com.awspaas.user.apps.finance.event.loan;

import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.ExecuteListener;
import com.actionsoft.bpms.util.DBSql;

public class LoanProcessCreateAfter extends ExecuteListener {

    @Override
    public String getDescription() {
        return "借款流程创建后，更新Status 变量值为 0 待提交";
    }

    @Override
    public void execute(ProcessExecutionContext processExecutionContext) throws Exception {
        String bindId = processExecutionContext.getProcessInstance().getId();
        DBSql.update("UPDATE BO_EU_LAON_APPLAY SET STATUS = '0' WHERE BINDID = ?",new Object[]{bindId});
    }
}
