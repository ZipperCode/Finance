package com.awspaas.user.apps.finance.event.loan;

import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.InterruptListener;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.exception.BPMNError;
import com.actionsoft.sdk.local.SDK;
import com.actionsoft.sdk.local.api.Logger;

public class LoanFistTaskBeforeComplete extends InterruptListener {
    Logger logger = SDK.getLogAPI().getLogger(this.getClass());
    @Override
    public boolean execute(ProcessExecutionContext processExecutionContext) throws Exception {
        String bindId = processExecutionContext.getProcessInstance().getId();
        // 更新流程状态为1 - 审批中
        DBSql.update("UPDATE BO_EU_LOAN_APPLAY SET APPLAY_STATUS=? WHERE BINDID= ?", new Object[]{"1",bindId});
        return true;
    }

    public String getDescription() {
        return "借款申请校验:TASK_BEFORE_COMPLETE 填写申请时校验，通过时-更新流程状态为申请中";
    }

}
