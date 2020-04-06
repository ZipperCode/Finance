package com.awspaas.user.apps.finance.event.bx;

import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.InterruptListener;
import com.actionsoft.bpms.commons.database.RowMap;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.sdk.local.SDK;
import com.actionsoft.sdk.local.api.Logger;
import com.awspaas.user.apps.finance.constant.FinanceConst;
import com.awspaas.user.apps.finance.util.StringUtil;

public class ReimFinTaskBeforeComplete extends InterruptListener {

    Logger logger = SDK.getLogAPI().getLogger(this.getClass());
    @Override
    public boolean execute(ProcessExecutionContext processExecutionContext) throws Exception {
        String bindId = processExecutionContext.getProcessInstance().getId();

        RowMap rowMap = DBSql.getMap("SELECT LOAN_NO,IS_PEOJECT_TRAVEL,PROJECT_NO,REIM_MONEY,BALANCE_MONEY,WRITE_OFF_MONEY  \n" +
                "FROM BO_EU_BX WHERE BINDID=?",bindId);
        double reimMoney = rowMap.getDouble("REIM_MONEY");
        log("本次报销金额 = " + reimMoney);
        // 比较待冲销金额和报销金额
        if(reimMoney > 0 && reimMoney < 1000){
            // 待冲销金额
            double balanceMoney = rowMap.getDouble("BALANCE_MONEY");
            log("待冲销金额 = "+ balanceMoney);
            // 申请通过后将借款单金额
//            if(!StringUtil.isEmpty(rowMap.getString("IS_PROJECT_TRAVEL"))){
//                log("是项目借款");
//
//            }else{
//                // 非项目报销
//            }
            String loanNo = rowMap.getString("LOAN_NO");
            log("关联借款单 - 借款单号为 loanNo = " + loanNo);
            if(!StringUtil.isEmpty(loanNo)){
                // 关联借款单
                DBSql.update("UPDATE BO_EU_LOAN_APPLAY SET WRITE_OFF_MONEY = WRITE_OFF_MONEY + ?,UN_WRITE_OFF_MONEY = UN_WRITE_OFF_MONEY - ? " +
                                "WHERE LOAN_NO = ?",
                        new Object[]{reimMoney,reimMoney,loanNo});
                // 如果本次报销金额刚好抵扣待冲销金额，将借款单冲销状态改为已完成
                if(reimMoney == balanceMoney){
                    DBSql.update("UPDATE BO_EU_LOAN_APPLAY SET WRITE_OFF_STATUS = '1' WHERE LOAN_NO = ?",new Object[]{loanNo});
                }
            }
            // 是否有借款单,如果有借款金额，将本次报销去冲销借款单未冲销金额

            // 财务审批后，将流程状态改为已完成
            DBSql.update("UPDATE BO_EU_BX SET STATUS = ? WHERE BINDID = ?", new Object[]{FinanceConst.HAS_AGREE, bindId});
        }
        return true;
    }

    private void log(String str){
        logger.info("【报销-财务审批】-----》" + str);
    }

    public String getDescription() {
        return "报销流程:财务审批-将流程的审批状态改为完成";
    }

}
