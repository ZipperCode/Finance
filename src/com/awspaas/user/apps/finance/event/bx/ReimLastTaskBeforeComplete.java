package com.awspaas.user.apps.finance.event.bx;

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

public class ReimLastTaskBeforeComplete extends InterruptListener {
    Logger logger = SDK.getLogAPI().getLogger(this.getClass());

    @Override
    public boolean execute(ProcessExecutionContext processExecutionContext) throws Exception {
        String bindId = processExecutionContext.getProcessInstance().getId();
        RowMap rowMap = DBSql.getMap("SELECT LOAN_NO,IS_PEOJECT_TRAVEL,PROJECT_NO,REIM_MONEY,BALANCE_MONEY,WRITE_OFF_MONEY  \n" +
                "FROM BO_EU_BX WHERE BINDID=?",bindId);
        Connection connection = DBSql.open();
        try{
            double reimMoney = rowMap.getDouble("REIM_MONEY");
            String loanNo = rowMap.getString("LOAN_NO");
            log("关联借款单 - 借款单号为 loanNo = " + loanNo);
            // 关联借款单
            double balanceMoney = rowMap.getDouble("BALANCE_MONEY");
            double writeOffMoney = rowMap.getDouble("WRITE_OFF_MONEY");
            log("冲销金额为 ：" + writeOffMoney +" , 待冲销金额为： " + balanceMoney + ", 报销金额为： " + reimMoney);
            connection.setAutoCommit(false);
            if(!StringUtil.isEmpty(loanNo)) {
                log("已关联借款单");
                DBSql.update(connection,"UPDATE BO_EU_LOAN_APPLAY SET WRITE_OFF_MONEY = WRITE_OFF_MONEY + ?,UN_WRITE_OFF_MONEY = UN_WRITE_OFF_MONEY - ? " +
                                "WHERE LOAN_NO = ?",
                        new Object[]{writeOffMoney, writeOffMoney, loanNo});
                // 如果本次报销金额刚好抵扣待冲销金额，将借款单冲销状态改为已完成
                if (writeOffMoney == balanceMoney) {
                    log("报销金额和待冲销金额相等");
                    DBSql.update(connection,"UPDATE BO_EU_LOAN_APPLAY SET WRITE_OFF_STATUS = '1' WHERE LOAN_NO = ?", new Object[]{loanNo});
                }
            }else{
                // WRITE_OFF_MONEY
                log("未关联借款单");
            }
            buildVoucher(connection,processExecutionContext);
            DBSql.update(connection,"UPDATE BO_EU_BX SET STATUS = ? WHERE BINDID = ?", new Object[]{FinanceConst.HAS_AGREE, bindId});
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
        RowMap rowMap = DBSql.getMap("SELECT * FROM BO_EU_BX WHERE BINDID = ?", bindId);
        System.out.println(rowMap);
        VoucherRise voucherRise = new VoucherRise();
        voucherRise.setBindId(bindId);
        voucherRise.setVoucherCode(processExecutionContext.execAtScript("VC-@year@month@dayofmonth@sequence('凭证头编号',5,0)"));
        String userNo = rowMap.getString("APPLAY_NAME");
        double reimMoney = rowMap.getDouble("REIM_MONEY");
        Date date = rowMap.getDate("APPLAY_DATE");
        voucherRise.setVoucherName(userNo + "的报销申请");
        voucherRise.setVoucherDate(DateUtil.formatdate(date));
        String voucherText = "["+DateUtil.formatdate(date)+"]-" + userNo + "的报销申请，报销金额为"+reimMoney + "元";
        String projectName = rowMap.getString("PROJECT_NAME");
        if(!StringUtil.isEmpty(projectName)){
            voucherText += ",出差项目为：" + projectName + ";";
        }
        String loanNo = rowMap.getString("LOAN_NO");
        if(!StringUtil.isEmpty(loanNo)){
            voucherText += "本次报销，对借款单["+loanNo+"]进行冲销";
        }
        voucherRise.setVoucherText(voucherText);
        voucherRise.setVoucherType(SDK.getDictAPI().getValue(APPID,  VOUCHER_TYPE_DICT,"0013"));
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
        String sql = "UPDATE BO_EU_BX SET VOUCHER_TITLE = ?,VOUCHER_DATE = ?,VOUCHER_PAY_NO = ?,VOUCHER_INFO = ?," +
                "VOUCHER_PAY_STATUS = '1',VOUCHER_PAY_DATE = ?, VOUCHER_REFER_NO = ?,VOUCHER_COMPANY_CODE = ?,VOUCHER_COMPANY_NAME = ? " +
                "WHERE BINDID = ?";
        DBSql.update(connection,sql,new Object[]{
                voucherRise.getVoucherName(),voucherRise.getVoucherDate(),voucherPayNo,voucherRise.getVoucherText(),
                DateUtil.formatdate(new Date()),voucherRise.getConsulNo(),voucherRise.getCompanyCode(),voucherRise.getCompanyName(),
                bindId
        });
    }

    private void log(String str){
        logger.info("【报销-经理审批】-----》" + str);
    }

    public String getDescription() {
        return "报销流程:经理审批-将流程的审批状态改为完成";
    }

}
