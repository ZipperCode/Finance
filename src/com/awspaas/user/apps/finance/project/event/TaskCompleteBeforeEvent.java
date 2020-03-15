package com.awspaas.user.apps.finance.project.event;

import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.InterruptListener;
import com.actionsoft.bpms.bpmn.engine.listener.InterruptListenerInterface;
import com.actionsoft.sdk.local.SDK;

public class TaskCompleteBeforeEvent extends InterruptListener {
    public String getDescription() {
        return "标记单据状态。选择'作废'更新为（9-已作废），其他均更新为（1-审批中）";
    }

    @Override
    public boolean execute(ProcessExecutionContext ctx) throws Exception {

        if (ctx.isChoiceActionMenu("作废")) {
            SDK.getBOAPI().updateByBindId("BO_EU_PROJECT", ctx.getProcessInstance().getId(), "STATUS", 9);
        } else {
            SDK.getBOAPI().updateByBindId("BO_EU_PROJECT", ctx.getProcessInstance().getId(), "STATUS", 1);
        }

        return false;
    }
}
