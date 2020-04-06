package com.awspaas.user.apps.finance.event.repay;

import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.InterruptListener;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.sdk.local.SDK;

public class RepayFirstTaskBeforeComplete extends InterruptListener {
    @Override
    public boolean execute(ProcessExecutionContext processExecutionContext) throws Exception {
        String bindId = processExecutionContext.getProcessInstance().getId();
        String loanNo = SDK.getBOAPI().getByProcess("BO_EU_REPAY",bindId,"LOAN_NO").toString();
        System.out.println("============================= BO 中借款单号为：" + loanNo);
        // 还款单冲销金额
        // double repayBalanceMoney = Double.parseDouble(SDK.getBOAPI().getByProcess("BO_EU_REPAY",bindId,"REPAY_BALANCE_MONEY").toString());
        // 更新借款单的冲销金额
        // String updateLoanSql = "UPDATE FROM BO_EU_LOAN_APPLAY SET WRITE_OFF_MONEY = WRITE_OFF_MONEY + "+repayBalanceMoney + "WHERE LOAN_NO = " + loanNo;
        //DBSql.update(updateLoanSql);
        // 更新待审批
        DBSql.update("UPDATE BO_EU_REPAY SET STATUS ='1' WHERE BINDID = '"+bindId+"'");
        return true;
    }

    public String getDescription() {
        return "还款申请流程: 冲销借款金额，将流程的审批状态改为待审";
    }

}
