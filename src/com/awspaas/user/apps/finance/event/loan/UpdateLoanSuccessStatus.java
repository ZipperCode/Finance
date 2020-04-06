package com.awspaas.user.apps.finance.event.loan;

import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.ExecuteListener;
import com.actionsoft.bpms.bpmn.engine.listener.InterruptListener;
import com.actionsoft.bpms.util.DBSql;
@Deprecated
public class UpdateLoanSuccessStatus extends ExecuteListener {

    @Override
    public String getDescription() {
        return "流程被总经理通过，更新流程状态为2";
    }

    @Override
    public void execute(ProcessExecutionContext processExecutionContext) throws Exception {
        try {
            processExecutionContext.setVariable("applayStatus", 2);
            DBSql.update("UPDATE BO_EU_LOAN_APPLAY SET PAY_STATUS='2' WHERE BINDID='" +
                    processExecutionContext.getProcessInstance().getId() + "'");
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }

    }
}
