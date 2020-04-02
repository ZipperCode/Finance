package com.awspaas.user.apps.finance.loan.event;

import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.ExecuteListener;
import com.actionsoft.bpms.bpmn.engine.listener.InterruptListener;
import com.actionsoft.bpms.util.DBSql;

public class UpdateLoanBackStatus extends InterruptListener {

    @Override
    public String getDescription() {
        return "流程被退回，更新流程状态为3";
    }

    @Override
    public boolean execute(ProcessExecutionContext processExecutionContext) throws Exception {
        try {
            if (!processExecutionContext.isChoiceActionMenu("不同意")) {
                return true;
            }
            DBSql.update("UPDATE BO_EU_LOAN_APPLAY SET STATUS='3' WHERE BINDID='" +
                    processExecutionContext.getProcessInstance().getId() + "'");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }

    }
}
