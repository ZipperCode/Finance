package com.awspaas.user.apps.finance.event.repay;

import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.ExecuteListener;
import com.actionsoft.sdk.local.SDK;

public class RepayProcessCreateAfter extends ExecuteListener {
    @Override
    public void execute(ProcessExecutionContext processExecutionContext) throws Exception {
        SDK.getBOAPI().updateByBindId("BO_EU_REPAY",
                processExecutionContext.getProcessInstance().getId(), "STATUS", 0);
    }
}
