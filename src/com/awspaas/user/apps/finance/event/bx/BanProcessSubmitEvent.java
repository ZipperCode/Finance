package com.awspaas.user.apps.finance.event.bx;

import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.InterruptListener;
import com.actionsoft.exception.BPMNError;
import com.actionsoft.sdk.local.SDK;

/**
 * 禁用流程提交
 */
public class BanProcessSubmitEvent extends InterruptListener {
    public String getDescription() {
        return "禁用流程提交";
    }
    @Override
    public boolean execute(ProcessExecutionContext processExecutionContext) throws Exception {
        //
        String bindId = processExecutionContext.getProcessInstance().getId();
        String status = (String) SDK.getBOAPI().get("BO_EU_BX_", bindId, "STATUS");
        if(status == null){
            throw new BPMNError("ERROR1", "status 为空，请联系管理员");
        }else{
            if("0".equals(status) || "3".equals(status)){
                // 待提交或者被驳回均可在此提交审核
                return true;
            }
        }
        throw new BPMNError("ERROR1", "流程已完成或者已作废，不可在此提交");
    }
}
