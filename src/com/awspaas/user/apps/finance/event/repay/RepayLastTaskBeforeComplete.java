package com.awspaas.user.apps.finance.event.repay;

import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.InterruptListener;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.sdk.local.SDK;
import com.actionsoft.sdk.local.api.Logger;
import com.awspaas.user.apps.finance.constant.FinanceConst;
import freemarker.template.utility.Execute;

import java.sql.Connection;

public class RepayLastTaskBeforeComplete extends InterruptListener {
    Logger logger = SDK.getLogAPI().getLogger(this.getClass());
    @Override
    public boolean execute(ProcessExecutionContext processExecutionContext) throws Exception {
        String bindId = processExecutionContext.getProcessInstance().getId();
        // 获取流程状态
        String status = SDK.getBOAPI().getByProcess("BO_EU_REPAY",bindId,"STATUS").toString();
        if (FinanceConst.HAS_AGREE.equals(status)) {
            Connection connection = DBSql.open();
            try{
                connection.setAutoCommit(false);
                // 流程状态标记已完成
                String loanNo = SDK.getBOAPI().getByProcess("BO_EU_REPAY",bindId,"LOAN_NO").toString();
                System.out.println("============================= BO 中借款单号为：" + loanNo);
                // 获取还款单冲销金额
                double repayBalanceMoney = Double.parseDouble(SDK.getBOAPI().getByProcess("BO_EU_REPAY",bindId,"REPAY_BALANCE_MONEY").toString());
                double repayMoney = Double.parseDouble(SDK.getBOAPI().getByProcess("BO_EU_REPAY",bindId,"REPAY_REAL_MONEY").toString());
                DBSql.update(connection,"UPDATE BO_EU_REPAY SET REPAY_BALANCE_MONEY = REPAY_BALANCE_MONEY + ?",new Object[]{repayBalanceMoney});
                logger.info("借款单冲销金额为："+repayBalanceMoney + ",还款单冲销金额为："+repayBalanceMoney + repayMoney +" 还款金额为：" + repayMoney);
                // 更新借款单的冲销金额和未冲销金额
                //String updateLoanSql = "UPDATE BO_EU_LOAN_APPLAY SET WRITE_OFF_MONEY = WRITE_OFF_MONEY + "+repayBalanceMoney + "WHERE LOAN_NO = " + loanNo;
                String updateLoanSql = "UPDATE BO_EU_LOAN_APPLAY SET WRITE_OFF_MONEY = WRITE_OFF_MONEY + "+repayMoney +
                        " , UN_WRITE_OFF_MONEY = LOAN_MONEY - WRITE_OFF_MONEY  WHERE LOAN_NO = '" + loanNo + "'";
                int state = DBSql.update(connection,updateLoanSql);
                logger.info("更新借款单冲销金额和未冲销金额：state = "+state);
                // 更新项目成本
                updateProjectCost(connection,loanNo);
                // 生成凭证信息
                buildVoucher();
                // 更新已支付
                DBSql.update(connection,"UPDATE BO_EU_REPAY SET STATUS ='4' WHERE BINDID = '"+bindId+"'");
                connection.commit();
            }catch (Exception e){
                connection.rollback();
                throw e;
            }
        }
        return true;
    }

    private void updateProjectCost(Connection connection,String loanNo)throws Exception{
        double writeOffMoney = DBSql.getDouble(connection,"SELECT WRITE_OFF_MONEY FROM BO_EU_LOAN_APPLAY WHERE LOAN_NO = '"+loanNo +"'","WRITE_OFF_MONEY");
        String projectNo = DBSql.getString(connection,"SELECT APPLAY_PROJECT_NO FROM BO_EU_LOAN_APPLAY WHERE LOAN_NO = '"+loanNo +"'","APPLAY_PROJECT_NO");
        logger.info("writeOffMoney = " + writeOffMoney + ", projectNo = " + projectNo);
        DBSql.update(connection,"UPDATE BO_EU_PROJECT SET PROJECT_COST = PROJECT_COST - ? WHERE PROJECT_NO = ?",new Object[]{writeOffMoney,projectNo});
    }

    private void buildVoucher() throws Exception{

    }

    public String getDescription() {
        return "还款申请流程:财务收款 冲销借款金额，将流程的审批状态改已支付";
    }

}
