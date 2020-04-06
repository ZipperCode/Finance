package com.awspaas.user.apps.finance.event.repay;

import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.ValueListener;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.exception.BPMNError;
import com.actionsoft.sdk.local.SDK;
import com.actionsoft.sdk.local.api.Logger;

public class RepayActivityAdhocBranch extends ValueListener {
    Logger logger = SDK.getLogAPI().getLogger(this.getClass());

    @Override
    public String getDescription() {
        return "金额校验：ERR02，检查还款是否已经冲销借款金额";
    }

    @Override
    public String execute(ProcessExecutionContext processExecutionContext) throws Exception {
        String bindId = processExecutionContext.getProcessInstance().getId();
        String loanNo = SDK.getBOAPI().getByProcess("BO_EU_REPAY", bindId, "LOAN_NO").toString();
        logger.info("【还款申请】----借款单号："+loanNo);
        double repayMoney = Double.parseDouble(SDK.getBOAPI().getByProcess("BO_EU_REPAY", bindId, "REPAY_REAL_MONEY").toString());
        logger.info("【还款申请】----还款金额为："+repayMoney);
        // 根据借款单号查询借款单冲销金额，与此次冲销金额相比是否可以冲销
        String loanMoneySql = "SELECT (LOAN_MONEY - WRITE_OFF_MONEY) AS CAN_WRITE_OFF_MONEY FROM BO_EU_LOAN_APPLAY WHERE LOAN_NO = '"+ loanNo +"' AND ISEND = 1";
        double canWriteOffMoney = DBSql.getDouble(loanMoneySql, "CAN_WRITE_OFF_MONEY");
        logger.info("【还款申请】----可冲销金额为："+canWriteOffMoney);
        if(canWriteOffMoney < repayMoney){
            throw new BPMNError("ERR01","借款单待冲销金额为【" + canWriteOffMoney + "】小于当前还款金额【"+repayMoney+"】，不允许审批");
        }
        return null;
    }
}
