package com.awspaas.user.apps.finance.event.loan;

import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.ExecuteListener;
import com.actionsoft.bpms.commons.database.RowMap;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.sdk.local.SDK;

public class LoanAlertMessage  extends ExecuteListener {

    public String getDescription() {
        return "对超出还款期限的借款单进行警告。规则=（计划还款日-申请日期）> 还款期限。如果还款期限被设置为0或小于0，此项功能被关闭";
    }

    @Override
    public void execute(ProcessExecutionContext processExecutionContext) throws Exception {
        // 取参数，判断功能是否开启

        String status = (String) SDK.getBOAPI().getByProcess("BO_EU_LOAN_APPLAY",
                processExecutionContext.getProcessInstance().getId(), "STATUS");
        if (status == null) {
            status = "0";
        }
        if (status.equals("0") || status.equals("1") || status.equals("2")) {
            String userID = (String) SDK.getBOAPI().getByProcess("BO_EU_LOAN_APPLAY",
                    processExecutionContext.getProcessInstance().getId(), "APPLAY_NO");
            if (userID == null) {
                userID = processExecutionContext.getUserContext().getUID();
            }
            alert(processExecutionContext,userID);
        }
    }

    private void alert(ProcessExecutionContext processExecutionContext,String userId){
        RowMap map = DBSql.getMap("select count(LOAN_NO) LOAN_COUNT,sum(UN_WRITE_OFF_MONEY) LOAN_MONEYS FROM BO_EU_LOAN_APPLAY where ISEND = 1 and " +
                "APPLAY_NO = ? and WRITE_OFF_MONEY <> LOAN_MONEY and WRITE_OFF_STATUS = '0'", userId);
        int loanCount = map.getInt("LOAN_COUNT");
        double loanMoneys = map.getDouble("LOAN_MONEYS");
        if(loanCount > 0){
            processExecutionContext.addAlertMessageWarn("BO_ACT_FIN_FEE_JK_APPLY",
                    "警告：申请人还有" + loanCount + "笔未还清的借款单,未还金额为：" + loanMoneys + "元");
        }

    }
}
