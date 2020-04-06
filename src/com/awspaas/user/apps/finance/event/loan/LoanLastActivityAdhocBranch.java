package com.awspaas.user.apps.finance.event.loan;

import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.ValueListener;
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
        String userId = SDK.getBOAPI().getByProcess("BO_EU_LOAN_APPLAY", bindId, "APPLAY_ID").toString();
        logger.info("获取 userid = " + userId);
        // 借款项目
        String projectNo = SDK.getBOAPI().getByProcess("BO_EU_LOAN_APPLAY", bindId, "APPLAY_PROJECT_NO").toString();
        logger.info("获取 projectNo = " + projectNo);
        // 包含项目借款,则获取借款金额
        double loanMoney = Double.parseDouble(SDK.getBOAPI().getByProcess("BO_EU_LOAN_APPLAY", bindId, "LOAN_MONEY").toString());
        double loanLimit = Double.parseDouble(SDK.getBOAPI().getByProcess("BO_EU_LOAN_APPLAY", bindId,"LOAN_LIMIT").toString());
        if (projectNo != null && !"".equals(projectNo)) {
            String sqlWriteOff = "SELECT SUM(WRITE_OFF_MONEY) TOTAL_WRITE_OFF_MONEY FROM BO_EU_LOAN_APPLAY WHERE APPLAY_NO = '"+userId+"' AND ISEND = '1' AND APPLAY_PROJECT_NO = '"+projectNo+"'";
            String sqlAll = "SELECT SUM(LOAN_MONEY) TOTAL_LOAN_MONEY FROM BO_EU_LOAN_APPLAY WHERE APPLAY_NO = '"+userId+"' AND ISEND = '1' AND APPLAY_PROJECT_NO = '"+projectNo+"'";
            double allLoanMoney = DBSql.getDouble(sqlAll,"TOTAL_LOAN_MONEY");
            double writeOffMoney =DBSql.getDouble(sqlWriteOff,"TOTAL_WRITE_OFF_MONEY");
            logger.info("借款金额为 ：" + loanMoney + ", 金额限制为 ：" + loanLimit + ", 用户借款金额为：" + allLoanMoney + ", 用户已冲销金额为 ：" + writeOffMoney);
            if(allLoanMoney - writeOffMoney + loanMoney > loanLimit){
                throw new Exception("当前本次借款加累计未核销借款已超过额度标准【" + loanLimit + "】,请冲销部分金额后,再进行借款");
            }
        }else{
            String sqlWriteOff = "SELECT SUM(WRITE_OFF_MONEY) as TOTAL_WRITE_OFF_MONEY FROM BO_EU_LOAN_APPLAY WHERE APPLAY_NO = '"+userId+"' AND ISEND = '1' AND IS_LOAN_PROJECT = 0";
            String sqlAll = "SELECT SUM(LOAN_MONEY) as TOTAL_LOAN_MONEY FROM BO_EU_LOAN_APPLAY WHERE APPLAY_NO = '"+userId+"' AND ISEND = '1' AND IS_LOAN_PROJECT = 0";
            double allLoanMoney = DBSql.getDouble(sqlAll,"TOTAL_LOAN_MONEY");
            double writeOffMoney =DBSql.getDouble(sqlWriteOff,"TOTAL_WRITE_OFF_MONEY");
            logger.info("借款金额为 ：" + loanMoney + ", 金额限制为 ：" + loanLimit + ", 用户借款金额为：" + allLoanMoney + ", 用户已冲销金额为 ：" + writeOffMoney);
            if(allLoanMoney - writeOffMoney + loanMoney > loanLimit){
                throw new Exception("当前本次借款加累计未核销借款已超过额度标准【" + loanLimit + "】,请冲销部分金额后,再进行借款");
            }
        }


        return null;
    }
}
