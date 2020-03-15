package com.awspaas.user.apps.finance.project.event;

import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.InterruptListener;
import com.actionsoft.sdk.local.SDK;

public class ProcessSuccessEvent extends InterruptListener {

    public String getDescription() {
        return "标记单据状态。选择'同意'更新为（2-已通过）, '退回' 更新为 3";
    }
    @Override
    public boolean execute(ProcessExecutionContext processExecutionContext) throws Exception {
        if(processExecutionContext.isChoiceActionMenu("同意")){
            SDK.getBOAPI().updateByBindId("BO_EU_PROJECT",
                    processExecutionContext.getProcessInstance().getId(), "STATUS", 2);
        } else if(processExecutionContext.isChoiceActionMenu("退回")){
            SDK.getBOAPI().updateByBindId("BO_EU_PROJECT",
                    processExecutionContext.getProcessInstance().getId(), "STATUS", 3);
        }
        return true;
    }
}
