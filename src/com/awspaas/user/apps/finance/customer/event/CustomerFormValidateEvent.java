package com.awspaas.user.apps.finance.customer.event;

import com.actionsoft.bpms.bo.engine.BO;
import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.InterruptListener;
import com.actionsoft.sdk.local.SDK;
import com.actionsoft.sdk.local.api.LogAPI;
import com.actionsoft.sdk.local.api.Logger;

public class CustomerFormValidateEvent extends InterruptListener {


    @Override
    public String getDescription() {
        return "客户表单校验-子表数据校验";
    }

    @Override
    public boolean execute(ProcessExecutionContext processExecutionContext) throws Exception {
        BO bo = SDK.getBOAPI().getByProcess("BO_EU_CUSTOMER", processExecutionContext.getProcessInstance().getId());
        Logger logger = SDK.getLogAPI().getLogger(this.getClass());
        logger.info("【processExecutionContext】 id = " + processExecutionContext.getProcessInstance().getId());
        logger.info("【BO】"+bo.toJson());

        return false;
    }
}
