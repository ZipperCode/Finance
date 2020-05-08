package com.awspaas.user.apps.finance.event.loan;

import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.ValueListener;
import com.actionsoft.bpms.commons.database.RowMap;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.exception.BPMNError;
import com.actionsoft.sdk.local.SDK;
import com.actionsoft.sdk.local.api.Logger;

public class LoanLastActivityAdhocBranch extends ValueListener {
    Logger logger = SDK.getLogAPI().getLogger(this.getClass());

    @Override
    public String getDescription() {
        return "金额校验：ERR02，财务审核时审核已完成的借款单金额累积，校验是否允许通过放款";
    }

    @Override
    public String execute(ProcessExecutionContext processExecutionContext) throws Exception {
        String bindId = processExecutionContext.getProcessInstance().getId();
//        String userId = SDK.getBOAPI().getByProcess("BO_EU_LOAN_APPLAY", bindId, "APPLAY_ID").toString();
        String userId = DBSql.getString("SELECT APPLAY_NO FROM BO_EU_LOAN_APPLAY WHERE BINDID = ?",new Object[]{bindId});
        logger.info("获取 userid = " + userId);
//        String projectNo = SDK.getBOAPI().getByProcess("BO_EU_LOAN_APPLAY", bindId, "APPLAY_PROJECT_NO").toString();
        String projectNo = DBSql.getString("SELECT APPLAY_PROJECT_NO FROM BO_EU_LOAN_APPLAY WHERE BINDID = ?",new Object[]{bindId});
        // 借款项目
        logger.info("获取 projectNo = " + projectNo);
        String sql = "SELECT LOAN_MONEY,LOAN_LIMIT,IFNULL(SUM(LOAN_MONEY),0) TOTAL_LOAN_MONEY,IFNULL(SUM(WRITE_OFF_MONEY),0) TOTAL_WRITE_OFF_MONEY FROM BO_EU_LOAN_APPLAY WHERE APPLAY_NO = ?  AND IS_LOAN_PROJECT = ? AND ISEND = 1";
        RowMap map = DBSql.getMap(sql,userId,projectNo != null && !"".equals(projectNo) ? 1:0);

        // 包含项目借款,则获取借款金额
//        double loanMoney = Double.parseDouble(SDK.getBOAPI().getByProcess("BO_EU_LOAN_APPLAY", bindId, "LOAN_MONEY").toString());
        double loanMoney = map.getDouble("LOAN_MONEY");
//        double loanLimit = Double.parseDouble(SDK.getBOAPI().getByProcess("BO_EU_LOAN_APPLAY", bindId,"LOAN_LIMIT").toString());
        double loanLimit = map.getDouble("LOAN_LIMIT");
        double totalLoanMoney = map.getDouble("TOTAL_LOAN_MONEY");
        double totalWriteOffMoney = map.getDouble("TOTAL_WRITE_OFF_MONEY");
        logger.info("用户["+userId+"]申请借款金额为 ：" + loanMoney + ", 金额限制为 ：" + loanLimit + ", 用户总借款金额为："
                + totalLoanMoney + ", 用户已冲销金额为 ：" + totalWriteOffMoney);
        if(totalLoanMoney - totalWriteOffMoney > loanLimit){
            throw new BPMNError("ERR02","该用户本次借款加累计未核销借款已超过额度标准【"
                    + loanLimit + "】，"+"本次借款金额为 ：" + loanMoney + ", 用户总借款金额为："
                    + totalLoanMoney + ", 用户总已冲销金额为 ：" + totalWriteOffMoney+",当前尚未冲销金额为"
                    +(totalLoanMoney - totalWriteOffMoney)+"，不允许办理");
        }

        return null;
    }
}
