package com.awspaas.user.apps.finance.project.event;

import com.actionsoft.bpms.bo.engine.BO;
import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.ExecuteListener;
import com.actionsoft.bpms.bpmn.engine.listener.ListenerConst;
import com.actionsoft.sdk.local.SDK;
import com.actionsoft.sdk.local.api.BOQueryAPI;

import java.util.List;

public class UpdateLoadFormDataEvent extends ExecuteListener {

    public String getDescription() {
        return "获取BO表数据填充到表单上 【测试】";
    }

    @Override
    public void execute(ProcessExecutionContext processExecutionContext) throws Exception {
        String bindId = "";
        System.out.println("【UpdateLoadFormDataEvent】 bindId = "+ bindId);
        BO bo = SDK.getBOAPI().getByProcess("BO_EU_PROJECT", bindId);
        List<BO> projectDetail = processExecutionContext.getBOQuery("BO_PROJECT_DETAIL").bindId(bindId).list();
        processExecutionContext.setParameter(ListenerConst.FORM_EVENT_PARAM_BODATA,bo);
        processExecutionContext.setParameter("ListenerConst.FORM_EVENT_PARAM_GRIDDATA",projectDetail);
    }
}
