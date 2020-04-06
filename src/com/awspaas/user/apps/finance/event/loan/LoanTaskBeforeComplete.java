package com.awspaas.user.apps.finance.event.loan;

import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.InterruptListener;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.sdk.local.SDK;
import com.actionsoft.sdk.local.api.Logger;

import static com.awspaas.user.apps.finance.constant.FinanceConst.*;

public class LoanTaskBeforeComplete extends InterruptListener {
    Logger logger = SDK.getLogAPI().getLogger(this.getClass());
    @Override
    public boolean execute(ProcessExecutionContext processExecutionContext) throws Exception {
        String bindId = processExecutionContext.getProcessInstance().getId();
        try {
            if (processExecutionContext.isChoiceActionMenu(DISAGREE)) {
                logger.info("更新状态为3 不同意");
                // 更新流程状态为3 - 退回
                DBSql.update("UPDATE BO_EU_LOAN_APPLAY SET STATUS='3' WHERE BINDID='" +
                        bindId + "'");
            }else{
                logger.info("更新状态为2 同意");
                // 更新流程状态为2 - 已完成
                DBSql.update("UPDATE BO_EU_LOAN_APPLAY SET APPLAY_STATUS=? WHERE BINDID= ?", new Object[]{"2",bindId});
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }
        return true;
    }

    public String getDescription() {
        return "借款申请校验:TASK_BEFORE_COMPLETE 上级通过校验，通过时-更新流程状态为同意，不通过更新退回";
    }

}
