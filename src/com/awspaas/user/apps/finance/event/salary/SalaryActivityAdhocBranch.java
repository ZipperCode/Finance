package com.awspaas.user.apps.finance.event.salary;

import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.ValueListener;
import com.actionsoft.bpms.util.DBSql;
import com.awspaas.user.apps.finance.event.base.ActivityAdhocBranch;

public class SalaryActivityAdhocBranch extends ActivityAdhocBranch {

    @Override
    public String getDescription() {
        return "系统自动指定下个节点的办理人";
    }

    @Override
    public String execute(ProcessExecutionContext processExecutionContext) throws Exception {
        TAG = "薪资自动后继参与者事件";
        String bindId = processExecutionContext.getProcessInstance().getId();
        log("bindId = " + bindId);
        String userId = DBSql.getString("SELECT SALARY_ID FROM BO_EU_SALARY WHERE BINDID = ?", new Object[]{bindId});
        log("userId = " + userId);
        return "obj_c8e0a92e907000012d32baa39330e3d0:"+userId;
    }
}
