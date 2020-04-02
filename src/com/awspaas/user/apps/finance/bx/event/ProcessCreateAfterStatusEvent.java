package com.awspaas.user.apps.finance.bx.event;

import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.ExecuteListener;
import com.actionsoft.sdk.local.SDK;

public class ProcessCreateAfterStatusEvent extends ExecuteListener {

    public String getDescription() {
        return "流程创建前标记单据状态为--待提交";
    }

    @Override
    public void execute(ProcessExecutionContext processExecutionContext) throws Exception {
        // 流程创建前 将状态改为待提交
        SDK.getBOAPI().updateByBindId("BO_EU_BX",
                processExecutionContext.getProcessInstance().getId(), "STATUS", 0);
    }
}
