package com.awspaas.user.apps.finance.project.event;

import com.actionsoft.bpms.bo.engine.BO;
import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.InterruptListener;
import com.actionsoft.bpms.bpmn.engine.listener.ListenerConst;
import com.actionsoft.sdk.local.SDK;

public class UpdateProjectDate extends InterruptListener {

    public String getDescription() {
        return "修改存在的项目信息";
    }
    @Override
    public boolean execute(ProcessExecutionContext ctx) throws Exception {

        //记录ID
        String boId = ctx.getParameterOfString(ListenerConst.FORM_EVENT_PARAM_BOID);
        //BO表记录，注意：该记录的数据如果被修改，将会体现到表单上，修改后不会直接持久化到数据库中
        BO boData = (BO) ctx.getParameter(ListenerConst.FORM_EVENT_PARAM_BODATA);
        return false;
    }
}
