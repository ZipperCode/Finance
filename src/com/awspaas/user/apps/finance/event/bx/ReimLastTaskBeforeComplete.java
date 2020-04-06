package com.awspaas.user.apps.finance.event.bx;

import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.InterruptListener;
import com.actionsoft.bpms.commons.database.RowMap;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.sdk.local.SDK;
import com.actionsoft.sdk.local.api.Logger;
import com.awspaas.user.apps.finance.constant.FinanceConst;
import com.awspaas.user.apps.finance.util.StringUtil;

public class ReimLastTaskBeforeComplete extends InterruptListener {
    Logger logger = SDK.getLogAPI().getLogger(this.getClass());

    @Override
    public boolean execute(ProcessExecutionContext processExecutionContext) throws Exception {
        String bindId = processExecutionContext.getProcessInstance().getId();
        RowMap rowMap = DBSql.getMap("SELECT LOAN_NO,IS_PEOJECT_TRAVEL,PROJECT_NO,REIM_MONEY,BALANCE_MONEY,WRITE_OFF_MONEY,  \n" +
                "FROM BO_EU_BX WHERE BINDID=?",bindId);
        double reimMoney = rowMap.getDouble("REIM_MONEY");
        String loanNo = rowMap.getString("LOAN_NO");
        log("关联借款单 - 借款单号为 loanNo = " + loanNo);
        // 关联借款单
        double balanceMoney = rowMap.getDouble("BALANCE_MONEY");
        double writeOffMoney = rowMap.getDouble("WRITE_OFF_MONEY");
        log("冲销金额为 ：" + writeOffMoney +" , 待冲销金额为： " + balanceMoney + ", 报销金额为： " + reimMoney);
        if(!StringUtil.isEmpty(loanNo)) {
            log("已关联借款单");
            DBSql.update("UPDATE BO_EU_LOAN_APPLAY SET WRITE_OFF_MONEY = WRITE_OFF_MONEY + ?,UN_WRITE_OFF_MONEY = UN_WRITE_OFF_MONEY - ? " +
                            "WHERE LOAN_NO = ?",
                    new Object[]{writeOffMoney, writeOffMoney, loanNo});
            // 如果本次报销金额刚好抵扣待冲销金额，将借款单冲销状态改为已完成
            if (writeOffMoney == balanceMoney) {
                log("报销金额和待冲销金额相等");
                DBSql.update("UPDATE BO_EU_LOAN_APPLAY SET WRITE_OFF_STATUS = '1' WHERE LOAN_NO = ?", new Object[]{loanNo});
            }
        }else{
            // WRITE_OFF_MONEY
            log("未关联借款单");

        }
        DBSql.update("UPDATE BO_EU_BX SET STATUS = ? WHERE BINDID = ?", new Object[]{FinanceConst.HAS_AGREE, bindId});
        return true;
    }

    private void log(String str){
        logger.info("【报销-经理审批】-----》" + str);
    }

    public String getDescription() {
        return "报销流程:经理审批-将流程的审批状态改为完成";
    }

}
