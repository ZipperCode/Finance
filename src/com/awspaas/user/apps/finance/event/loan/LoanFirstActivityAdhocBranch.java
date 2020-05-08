package com.awspaas.user.apps.finance.event.loan;

import com.actionsoft.bpms.bo.engine.BO;
import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.InterruptListener;
import com.actionsoft.bpms.bpmn.engine.listener.ValueListener;
import com.actionsoft.bpms.commons.database.RowMap;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.exception.BPMNError;
import com.actionsoft.sdk.local.SDK;
import com.actionsoft.sdk.local.api.LogAPI;
import com.actionsoft.sdk.local.api.Logger;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Map;

public class LoanFirstActivityAdhocBranch extends ValueListener {
    Logger logger = SDK.getLogAPI().getLogger(this.getClass());

    @Override
    public String getDescription() {
        return "金额校验：检查累积借款金额是否超过限制借款金额,ERR01-项目，ERR02-普通";
    }

//    @Override
//    public String execute(ProcessExecutionContext processExecutionContext) throws Exception {
//        String bindId = processExecutionContext.getProcessInstance().getId();
//        String userId = SDK.getBOAPI().getByProcess("BO_EU_LOAN_APPLAY", bindId, "APPLAY_NO").toString();
//        logger.info("获取 userid = " + userId);
//        // 借款项目
//        String projectNo = SDK.getBOAPI().getByProcess("BO_EU_LOAN_APPLAY", bindId, "APPLAY_PROJECT_NO").toString();
//        logger.info("获取 projectNo = " + projectNo);
//        // 包含项目借款,则获取借款金额
//        double loanMoney = Double.parseDouble(SDK.getBOAPI().getByProcess("BO_EU_LOAN_APPLAY", bindId, "LOAN_MONEY").toString());
//        double loanLimit = Double.parseDouble(SDK.getBOAPI().getByProcess("BO_EU_LOAN_APPLAY", bindId,"LOAN_LIMIT").toString());
//        if (projectNo != null && !"".equals(projectNo)) {
//            String sqlWriteOff = "SELECT SUM(WRITE_OFF_MONEY) TOTAL_WRITE_OFF_MONEY FROM BO_EU_LOAN_APPLAY WHERE APPLAY_NO = '"+userId+"' AND APPLAY_PROJECT_NO = '"+projectNo+"'";
//            String sqlAll = "SELECT SUM(LOAN_MONEY) TOTAL_LOAN_MONEY FROM BO_EU_LOAN_APPLAY WHERE APPLAY_NO = '"+userId+"'  AND APPLAY_PROJECT_NO = '"+projectNo+"'";
//            double allLoanMoney = DBSql.getDouble(sqlAll,"TOTAL_LOAN_MONEY");
//            double writeOffMoney =DBSql.getDouble(sqlWriteOff,"TOTAL_WRITE_OFF_MONEY");
//            logger.info("借款金额为 ：" + loanMoney + ", 金额限制为 ：" + loanLimit + ", 用户借款金额为：" + allLoanMoney + ", 用户已冲销金额为 ：" + writeOffMoney);
//            if(allLoanMoney - writeOffMoney > loanLimit){
//                throw new BPMNError("ERR02","当前本次借款加累计未核销借款已超过额度标准【"
//                        + loanLimit + "】，"+"本次借款金额为 ：" + loanMoney + ", 用户总借款金额为："
//                        + allLoanMoney + ", 用户总已冲销金额为 ：" + writeOffMoney+",当前尚未冲销金额为"+(allLoanMoney - writeOffMoney)+"请冲销部分金额后,再进行借款");
//            }
//
//
//        }else{
////            String sqlWriteOff = "SELECT SUM(WRITE_OFF_MONEY) as TOTAL_WRITE_OFF_MONEY FROM BO_EU_LOAN_APPLAY WHERE APPLAY_NO = '"+userId+"'  AND IS_LOAN_PROJECT = 0";
////            String sqlAll = "SELECT SUM(LOAN_MONEY) as TOTAL_LOAN_MONEY FROM BO_EU_LOAN_APPLAY WHERE APPLAY_NO = '"+userId+"' AND IS_LOAN_PROJECT = 0";
////            double allLoanMoney = DBSql.getDouble(sqlAll,"TOTAL_LOAN_MONEY");
////            double writeOffMoney =DBSql.getDouble(sqlWriteOff,"TOTAL_WRITE_OFF_MONEY");
////            logger.info("借款金额为 ：" + loanMoney + ", 金额限制为 ：" + loanLimit + ", 用户借款金额为："
////                    + allLoanMoney + ", 用户已冲销金额为 ：" + writeOffMoney);
////            if(allLoanMoney - writeOffMoney > loanLimit){
////                throw new BPMNError("ERR02","当前本次借款加累计未核销借款已超过额度标准【"
////                        + loanLimit + "】，"+"本次借款金额为 ：" + loanMoney + ", 用户总借款金额为："
////                        + allLoanMoney + ", 用户总已冲销金额为 ：" + writeOffMoney+",当前尚未冲销金额为"+(allLoanMoney - writeOffMoney)+"请冲销部分金额后,再进行借款");
////            }
//            String sql = "SELECT IFNULL(SUM(LOAN_MONEY),0) TOTAL_LOAN_MONEY,IFNULL(SUM(WRITE_OFF_MONEY),0) TOTAL_WRITE_OFF_MONEY FROM BO_EU_LOAN_APPLAY WHERE APPLAY_NO = ?  AND IS_LOAN_PROJECT = 0 AND ISEND = 1";
//            RowMap map = DBSql.getMap(sql,userId);
//            double totalLoanMoney = map.getDouble("TOTAL_LOAN_MONEY");
//            double totalWriteOffMoney = map.getDouble("TOTAL_WRITE_OFF_MONEY");
//            logger.info("借款金额为 ：" + loanMoney + ", 金额限制为 ：" + loanLimit + ", 用户借款金额为："
//                    + totalLoanMoney + ", 用户已冲销金额为 ：" + totalWriteOffMoney);
//            if(totalLoanMoney - totalWriteOffMoney > loanLimit){
//                throw new BPMNError("ERR02","当前本次借款加累计未核销借款已超过额度标准【"
//                        + loanLimit + "】，"+"本次借款金额为 ：" + loanMoney + ", 用户总借款金额为："
//                        + totalLoanMoney + ", 用户总已冲销金额为 ：" + totalWriteOffMoney+",当前尚未冲销金额为"
//                        +(totalLoanMoney - totalWriteOffMoney)+"请冲销部分金额后,再进行借款");
//            }
//
//        }
//
//        return null;
//    }

    @Override
    public String execute(ProcessExecutionContext processExecutionContext) throws Exception {
        String bindId = processExecutionContext.getProcessInstance().getId();
        String userId = SDK.getBOAPI().getByProcess("BO_EU_LOAN_APPLAY", bindId, "APPLAY_NO").toString();
        logger.info("获取 userid = " + userId);
        // 借款项目
        String projectNo = SDK.getBOAPI().getByProcess("BO_EU_LOAN_APPLAY", bindId, "APPLAY_PROJECT_NO").toString();
        logger.info("获取 projectNo = " + projectNo);
        // 包含项目借款,则获取借款金额
        double loanMoney = Double.parseDouble(SDK.getBOAPI().getByProcess("BO_EU_LOAN_APPLAY", bindId, "LOAN_MONEY").toString());
        double loanLimit = Double.parseDouble(SDK.getBOAPI().getByProcess("BO_EU_LOAN_APPLAY", bindId,"LOAN_LIMIT").toString());
        String sql = "SELECT IFNULL(SUM(LOAN_MONEY),0) TOTAL_LOAN_MONEY,IFNULL(SUM(WRITE_OFF_MONEY),0) TOTAL_WRITE_OFF_MONEY FROM BO_EU_LOAN_APPLAY WHERE APPLAY_NO = ?  AND IS_LOAN_PROJECT = ? AND ISEND = 1";
        RowMap map = DBSql.getMap(sql,userId,projectNo != null && !"".equals(projectNo) ? 1:0);
        double totalLoanMoney = map.getDouble("TOTAL_LOAN_MONEY");
        double totalWriteOffMoney = map.getDouble("TOTAL_WRITE_OFF_MONEY");
        logger.info("借款金额为 ：" + loanMoney + ", 金额限制为 ：" + loanLimit + ", 用户借款金额为："
                + totalLoanMoney + ", 用户已冲销金额为 ：" + totalWriteOffMoney);
        if(totalLoanMoney - totalWriteOffMoney > loanLimit){
            throw new BPMNError("ERR02","当前本次借款加累计未核销借款已超过额度标准【"
                    + loanLimit + "】，"+"本次借款金额为 ：" + loanMoney + ", 用户总借款金额为："
                    + totalLoanMoney + ", 用户总已冲销金额为 ：" + totalWriteOffMoney+",当前尚未冲销金额为"
                    +(totalLoanMoney - totalWriteOffMoney)+"请冲销部分金额后,再进行借款");
        }
        return null;
    }
}
