package com.awspaas.user.apps.finance.event.repay;

import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.InterruptListener;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.exception.BPMNError;
import com.actionsoft.sdk.local.SDK;
import com.actionsoft.sdk.local.api.Logger;
import com.awspaas.user.apps.finance.util.PublicExceptionHint;

@Deprecated
public class RepayFormCompleteValid extends InterruptListener {

    Logger logger = SDK.getLogAPI().getLogger(this.getClass());
    @Override
    public boolean execute(ProcessExecutionContext processExecutionContext) throws Exception {
        String bindId = processExecutionContext.getProcessInstance().getId();
        try{
            // 获取还款金额
            double repayMoney = Double.parseDouble(SDK.getBOAPI().getByProcess("BO_EU_REPAY", bindId, "REPAY_REAL_MONEY").toString());
            // 获取借款单
            String loanNo = SDK.getBOAPI().getByProcess("BO_EU_REPAY",bindId,"LOAN_NO").toString();
            // 根据借款单获取借款金额
            double loanMoney = DBSql.getDouble("SELECT LOAN_MONEY FROM BO_EU_LOAN_APPLAY WHERE LOAN_NO = " + loanNo,"LOAN_MONEY");
            double writeOffMoney = DBSql.getDouble("SELECT WRITE_OFF_MONEY FROM BO_EU_LOAN_APPLAY WHERE LOAN_NO = " + loanNo,"WRITE_OFF_MONEY");
            if(repayMoney > (loanMoney - writeOffMoney)){
                throw new BPMNError("ERR01","还款金额大于应该归还金额，请检查");
            }
        }catch (Exception e){
            e.printStackTrace();
            PublicExceptionHint.unifyThrowErrorMessage(e);
        }
        return true;
    }

    public String getDescription() {
        return "还款申请:还款金额校验";
    }

}
