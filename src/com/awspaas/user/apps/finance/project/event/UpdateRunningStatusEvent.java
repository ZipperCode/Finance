package com.awspaas.user.apps.finance.project.event;

import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.ExecuteListener;
import com.actionsoft.bpms.bpmn.engine.listener.ValueListener;
import com.actionsoft.sdk.local.SDK;

public class UpdateRunningStatusEvent extends ExecuteListener {

    @Override
    public String getDescription() {
        return "提交审批后，更新项目审批流程状态为审批中";
    }

    @Override
    public void execute(ProcessExecutionContext processExecutionContext) throws Exception {
        SDK.getBOAPI().updateByBindId("BO_EU_PROJECT",
                processExecutionContext.getProcessInstance().getId(), "STATUS", 1);
        processExecutionContext.setVariable("status", 1);
    }
}
