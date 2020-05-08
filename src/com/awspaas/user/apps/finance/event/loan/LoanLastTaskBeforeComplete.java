package com.awspaas.user.apps.finance.event.loan;

import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.InterruptListener;
import com.actionsoft.bpms.commons.database.RowMap;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.sdk.local.SDK;
import com.actionsoft.sdk.local.api.Logger;
import com.awspaas.user.apps.finance.bo.VoucherRise;
import com.awspaas.user.apps.finance.constant.FinanceConst;
import com.awspaas.user.apps.finance.util.DateUtil;
import com.awspaas.user.apps.finance.util.StringUtil;
import com.awspaas.user.apps.finance.util.TransUtil;
import com.awspaas.user.apps.finance.voucher.VoucherFactory;

import java.sql.Connection;
import java.util.Date;

import static com.awspaas.user.apps.finance.constant.FinanceConst.APPID;
import static com.awspaas.user.apps.finance.constant.FinanceConst.VOUCHER_TYPE_DICT;

public class LoanLastTaskBeforeComplete extends InterruptListener {

    Logger logger = SDK.getLogAPI().getLogger(this.getClass());
    @Override
    public boolean execute(ProcessExecutionContext processExecutionContext) throws Exception {
        System.out.println("execute");
        Connection connection = DBSql.open();
        try{
            String bindId = processExecutionContext.getProcessInstance().getId();
            System.out.println("流程ID = " + bindId);
            RowMap rowMap = DBSql.getMap("SELECT IS_LOAN_PROJECT,APPLAY_PROJECT_NO,LOAN_MONEY FROM BO_EU_LOAN_APPLAY WHERE BINDID = ?",bindId);
            System.out.println("rowMap = " + rowMap);
            connection.setAutoCommit(false);
            if(rowMap != null && !rowMap.isEmpty()){
                // 判断是否项目出差，如果是项目出差，则借款金额应该算在成本中
                String loanProject = rowMap.getString("IS_LOAN_PROJECT");
                System.out.println("是否项目借款");
                if(FinanceConst.YES.equals(loanProject)){
                    System.out.println("是否项目借款：是");
                    String projectNo = rowMap.getString("APPLAY_PROJECT_NO");
                    double loanMoney = rowMap.getDouble("LOAN_MONEY");
                    if(!StringUtil.isEmpty(projectNo)){
                        DBSql.update(connection,"UPDATE BO_EU_PROJECT SET PROJECT_COST = PROJECT_COST + ? WHERE PROJECT_NO = ?",new Object[]{loanMoney,projectNo});
                    }
                }
            }
            System.out.println("构建凭证头");
            buildVoucher(connection,processExecutionContext);
            DBSql.update(connection,"UPDATE BO_EU_LOAN_APPLAY SET APPLAY_STATUS=? ,PAY_DATE = NOW(),UN_WRITE_OFF_MONEY = LOAN_MONEY WHERE BINDID= ?", new Object[]{"4",bindId});
            connection.commit();
        }catch (Exception e){
            connection.rollback();
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private void buildVoucher(Connection connection,ProcessExecutionContext processExecutionContext) throws Exception{
        String bindId = processExecutionContext.getProcessInstance().getId();
        System.out.println("bindId = " + bindId);
        RowMap rowMap = DBSql.getMap("SELECT * FROM BO_EU_LOAN_APPLAY WHERE BINDID = ?", bindId);
        System.out.println(rowMap);
        VoucherRise voucherRise = new VoucherRise();
        voucherRise.setBindId(bindId);
        voucherRise.setVoucherCode(processExecutionContext.execAtScript("VC-@year@month@dayofmonth@sequence('凭证头编号',5,0)"));
        String userNo = rowMap.getString("APPLAY_NAME");
        double money = rowMap.getDouble("LOAN_MONEY");
        String receiveUser = rowMap.getString("RECEIVE_NAME");
        Date date = rowMap.getDate("APPLAY_DATE");
        voucherRise.setVoucherName(userNo + "的"+money + "元借款申请");
        voucherRise.setVoucherDate(DateUtil.formatdate(date));
        voucherRise.setVoucherText("["+DateUtil.formatdate(date)+"]-" + userNo + "的"+money + "元申请，收款人："+receiveUser);
        voucherRise.setVoucherType(SDK.getDictAPI().getValue(APPID,  VOUCHER_TYPE_DICT,"0011"));
        voucherRise.setAccountYear(DateUtil.currentDate());
        voucherRise.setAcDocNo(processExecutionContext.execAtScript("AC-@year@month@dayofmonth@sequence('凭证编号',5,0)"));
        voucherRise.setCompanyCode(processExecutionContext.execAtScript("@companyNo"));
        voucherRise.setCompanyName(processExecutionContext.execAtScript("@companyName"));
        voucherRise.setBusDocType("");
        voucherRise.setBussSys("");
        voucherRise.setCurrencyCode("RMB");
        voucherRise.setConsulNo(processExecutionContext.execAtScript("@sequence('参照号',10,0)"));
        voucherRise.setPostDate(DateUtil.formatdate(new Date()));
        voucherRise.setRefCode("");
        voucherRise.setTransDate(DateUtil.formatdate(new Date()));
        System.out.println(voucherRise.toString());
        VoucherFactory.insertVoucherRise(connection, TransUtil.voucher2Map(voucherRise));
        String voucherPayNo = processExecutionContext.execAtScript("VCP-@year@month@dayofmonth@sequence('凭证支付编号',5,0)");
        String sql = "UPDATE BO_EU_LOAN_APPLAY SET VOUCHER_TITLE = ?,VOUCHER_DATE = ?,VOUCHER_PAY_NO = ?,VOUCHER_INFO = ?," +
                "VOUCHER_PAY_STATUS = '1',VOUCHER_PAY_DATE = ?, VOUCHER_REFER_NO = ?,VOUCHER_COMPANY_CODE = ?,VOUCHER_COMPANY_NAME = ? " +
                "WHERE BINDID = ?";
        DBSql.update(connection,sql,new Object[]{
                voucherRise.getVoucherName(),voucherRise.getVoucherDate(),voucherPayNo,voucherRise.getVoucherText(),
                DateUtil.formatdate(new Date()),voucherRise.getConsulNo(),voucherRise.getCompanyCode(),voucherRise.getCompanyName(),
                bindId
        });
    }

    private void log(String s){
        logger.info("【借款单申请】----》" + s);
    }

    public String getDescription() {
        return "借款申请校验:TASK_BEFORE_COMPLETE 财务通过时校验，通过时-更新流程状态为已支付,更新支付时间为当前时间";
    }
}
