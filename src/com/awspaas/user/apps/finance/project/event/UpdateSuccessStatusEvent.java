package com.awspaas.user.apps.finance.project.event;

import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.ExecuteListener;
import com.actionsoft.sdk.local.SDK;

public class UpdateSuccessStatusEvent extends ExecuteListener {

    @Override
    public String getDescription() {
        return "审批完成后，更新项目审批流程状态为已完成";
    }

    @Override
    public void execute(ProcessExecutionContext processExecutionContext) throws Exception {
        SDK.getBOAPI().updateByBindId("BO_EU_PROJECT",
                processExecutionContext.getProcessInstance().getId(), "STATUS", 2);
        processExecutionContext.setVariable("status", 2);
    }
}
