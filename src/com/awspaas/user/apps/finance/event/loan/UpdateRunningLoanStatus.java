package com.awspaas.user.apps.finance.event.loan;

import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.ExecuteListener;
import com.actionsoft.sdk.local.SDK;

@Deprecated
public class UpdateRunningLoanStatus extends ExecuteListener {

    @Override
    public String getDescription() {
        return "提交审批后，更新流程状态为审批中";
    }

    @Override
    public void execute(ProcessExecutionContext processExecutionContext) throws Exception {
        SDK.getBOAPI().updateByBindId("BO_EU_LOAN_APPLAY",
                processExecutionContext.getProcessInstance().getId(), "APPLAY_STATUS", 1);
        processExecutionContext.setVariable("applayStatus", 1);
    }
}
