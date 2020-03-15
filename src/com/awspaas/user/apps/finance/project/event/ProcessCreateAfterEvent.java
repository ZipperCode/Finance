package com.awspaas.user.apps.finance.project.event;

import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.ExecuteListener;
import com.actionsoft.bpms.bpmn.engine.listener.InterruptListener;
import com.actionsoft.sdk.local.SDK;

public class ProcessCreateAfterEvent extends ExecuteListener {

    public String getDescription() {
        return "流程创建前标记单据状态为--待提交";
    }

    @Override
    public void execute(ProcessExecutionContext processExecutionContext) throws Exception {
        // 流程创建前 将状态改为待提交
        SDK.getBOAPI().updateByBindId("BO_EU_PROJECT",
                processExecutionContext.getProcessInstance().getId(), "STATUS", 0);
    }
}
