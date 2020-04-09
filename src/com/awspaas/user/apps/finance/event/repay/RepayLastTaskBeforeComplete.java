package com.awspaas.user.apps.finance.event.repay;

import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.InterruptListener;
import com.actionsoft.bpms.commons.database.RowMap;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.sdk.local.SDK;
import com.actionsoft.sdk.local.api.Logger;
import com.awspaas.user.apps.finance.bo.VoucherRise;
import com.awspaas.user.apps.finance.constant.FinanceConst;
import com.awspaas.user.apps.finance.util.DateUtil;
import com.awspaas.user.apps.finance.util.TransUtil;
import com.awspaas.user.apps.finance.voucher.VoucherFactory;
import freemarker.template.utility.Execute;

import java.sql.Connection;
import java.util.Date;

import static com.awspaas.user.apps.finance.constant.FinanceConst.APPID;
import static com.awspaas.user.apps.finance.constant.FinanceConst.VOUCHER_TYPE_DICT;

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
                buildVoucher(connection,processExecutionContext);
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

    private void buildVoucher(Connection connection,ProcessExecutionContext processExecutionContext) throws Exception{
        String bindId = processExecutionContext.getProcessInstance().getId();
        RowMap rowMap = DBSql.getMap("SELECT * FROM BO_EU_REPAY WHERE BINDID = ?", bindId);
        VoucherRise voucherRise = new VoucherRise();
        voucherRise.setBindId(bindId);
        voucherRise.setVoucherCode(processExecutionContext.execAtScript("VC-@year@month@dayofmonth@sequence('凭证头编号',5,0)"));
        String userNo = rowMap.getString("APPLAY_NAME");
        double money = rowMap.getDouble("REPAY_REAL_MONEY");
        double repayMoney = rowMap.getDouble("REPAY_MONEY");
        String loanUser = rowMap.getString("LOAN_ACCOUNT_NAME");
        double loanMoney = rowMap.getDouble("LOAN_MONEY");
        Date date = rowMap.getDate("APPLAY_DATE");
        voucherRise.setVoucherName(userNo + "的"+money + "元还款申请");
        voucherRise.setVoucherDate(DateUtil.formatdate(date));
        voucherRise.setVoucherText("["+DateUtil.formatdate(date)+"]-" + userNo + "的"+money + "元还款申请，剩余待还："
                +(repayMoney - money)+"借款人："+loanUser + ",借款金额：" + loanMoney);
        voucherRise.setVoucherType(SDK.getDictAPI().getValue(APPID,  VOUCHER_TYPE_DICT,"0008"));
        voucherRise.setAccountYear(DateUtil.currentDate());
        voucherRise.setAcDocNo(processExecutionContext.execAtScript("AC-@year@month@dayofmonth@sequence('凭证编号',5,0)"));
        voucherRise.setCompanyCode(processExecutionContext.execAtScript("@companyNo"));
        voucherRise.setCompanyName(processExecutionContext.execAtScript("@companyName"));
        voucherRise.setBussSys("");
        voucherRise.setBusDocType("");
        voucherRise.setCurrencyCode("RMB");
        voucherRise.setConsulNo(processExecutionContext.execAtScript("@sequence('参照号',10,0)"));
        voucherRise.setRefCode("");
        voucherRise.setPostDate(DateUtil.formatdate(new Date()));
        voucherRise.setTransDate(DateUtil.formatdate(new Date()));
        VoucherFactory.insertVoucherRise(connection, TransUtil.voucher2Map(voucherRise));
        String voucherPayNo = processExecutionContext.execAtScript("VCP-@year@month@dayofmonth@sequence('凭证支付编号',5,0)");
        System.out.println(voucherRise.toString());
        DBSql.update(connection,"UPDATE BO_EU_REPAY SET VOUCHER_TITLE = ?,VOUCHER_DATE = ?,VOUCHER_PAY_NO = ?,VOUCHER_INFO = ?," +
                "VOUCHER_PAY_STATUS = '1',VOUCHER_PAY_DATE = ?, VOUCHER_REFER_NO = ?,VOUCHER_COMPANY_CODE = ?,VOUCHER_COMPANY_NAME = ? " +
                        "WHERE BINDID = ?",
                new Object[]{
                        voucherRise.getVoucherName(),voucherRise.getVoucherDate(),voucherPayNo,voucherRise.getVoucherText(),
                DateUtil.formatdate(new Date()),voucherRise.getConsulNo(),voucherRise.getCompanyCode(),voucherRise.getCompanyName(),
                bindId
        });
    }


    public String getDescription() {
        return "还款申请流程:财务收款 冲销借款金额，将流程的审批状态改已支付";
    }

}
