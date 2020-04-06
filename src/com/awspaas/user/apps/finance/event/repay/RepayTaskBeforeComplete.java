package com.awspaas.user.apps.finance.event.repay;

import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.InterruptListener;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.sdk.local.SDK;
import com.actionsoft.sdk.local.api.Logger;

import static com.awspaas.user.apps.finance.constant.FinanceConst.AGREE;

public class RepayTaskBeforeComplete extends InterruptListener {
    Logger logger = SDK.getLogAPI().getLogger(this.getClass());
    @Override
    public boolean execute(ProcessExecutionContext processExecutionContext) throws Exception {
        String bindId = processExecutionContext.getProcessInstance().getId();
        String loanNo = SDK.getBOAPI().getByProcess("BO_EU_REPAY",bindId,"LOAN_NO").toString();
        System.out.println("============================= BO 中借款单号为：" + loanNo);
        // 还款单冲销金额
        if(processExecutionContext.isChoiceActionMenu(AGREE)){
            logger.info("选择了同意，将流程状态设置为2 已完成");
            DBSql.update("UPDATE BO_EU_REPAY SET STATUS ='2' WHERE BINDID = '"+bindId +"'");
        }else{
            logger.info("选择了同意，将流程状态设置为3 已退回");
            DBSql.update("UPDATE BO_EU_REPAY SET STATUS ='3' WHERE BINDID = '"+bindId +"'");
        }
        return true;
    }

    public String getDescription() {
        return "还款申请流程: 冲销借款金额，将流程的审批状态改为完成";
    }

}
