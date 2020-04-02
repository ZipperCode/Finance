package com.awspaas.user.apps.finance.loan.event;

import com.actionsoft.bpms.bo.engine.BO;
import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.InterruptListener;
import com.actionsoft.exception.BPMNError;
import com.actionsoft.sdk.local.SDK;
import com.actionsoft.sdk.local.api.LogAPI;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class LoanFormValidateEvent extends InterruptListener {

    @Override
    public String getDescription() {
        return "日期校验：ERR01，借款日期不能小于还款日期" +
                "金额校验：ERR02，借款金额不能大于允许借款的金额";
    }

    @Override
    public boolean execute(ProcessExecutionContext processExecutionContext) throws Exception {
        BO bo = SDK.getBOAPI().getByProcess("BO_EU_LOAN_APPLAY", processExecutionContext.getProcessInstance().getId());
//        LogAPI.getLogger("BO:"+bo);
//        Timestamp loanDate = bo.get("LOAN_DATE", Timestamp.class);
////        Timestamp loanReturnDate = bo.get("LOAN_RETURN_DATE", Timestamp.class);
////        LogAPI.getLogger("loanDate:"+loanDate + ",loanReturnDate:"+loanReturnDate);
////        if(loanDate.toLocalDateTime().toLocalDate().isBefore(loanReturnDate.toLocalDateTime().toLocalDate())){
////            throw new BPMNError("ERR01","借款日期不能小于还款日期");
//        }
        try{
            BigDecimal limitMoney = bo.get("LOAN_LIMIT",BigDecimal.class);
            BigDecimal loanMoney = bo.get("LOAN_MONEY", BigDecimal.class);
            System.out.println("限制金额为："+limitMoney.doubleValue() + ",借款金额为："+loanMoney.floatValue());
            if(limitMoney.compareTo(loanMoney) > 0 ){
                throw new BPMNError("ERR02","借款金额不能大于允许借款的金额");
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return true;
    }
}
