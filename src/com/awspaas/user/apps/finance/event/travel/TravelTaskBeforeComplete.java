package com.awspaas.user.apps.finance.event.travel;

import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.commons.database.RowMap;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.sdk.local.SDK;
import com.awspaas.user.apps.finance.bo.VoucherRise;
import com.awspaas.user.apps.finance.event.base.TaskBeforeComplete;
import com.awspaas.user.apps.finance.util.DateUtil;
import com.awspaas.user.apps.finance.util.TransUtil;
import com.awspaas.user.apps.finance.voucher.VoucherFactory;

import java.sql.Connection;
import java.util.Date;

import static com.awspaas.user.apps.finance.constant.FinanceConst.*;

public class TravelTaskBeforeComplete extends TaskBeforeComplete {
    @Override
    public boolean execute(ProcessExecutionContext processExecutionContext) throws Exception {
        TAG = "【出差申请-财务】";
        String bindId = processExecutionContext.getProcessInstance().getId();
        Connection connection = DBSql.open();
        try{
            connection.setAutoCommit(false);
            DBSql.update(connection,"UPDATE BO_EU_TRAVEL SET STATUS = ? WHERE BINDID=?",new Object[]{HAS_AGREE,bindId});
            buildVoucher(connection,processExecutionContext);
            connection.commit();
        }catch (Exception e){
            connection.rollback();
            throw e;
        }
        return true;
    }

    private void buildVoucher(Connection connection,ProcessExecutionContext processExecutionContext) throws Exception{
        log("开始生成凭证信息");
        String bindId = processExecutionContext.getProcessInstance().getId();
        RowMap rowMap = DBSql.getMap("SELECT * FROM BO_EU_TRAVEL WHERE BINDID = ?", bindId);
        VoucherRise voucherRise = new VoucherRise();
        voucherRise.setBindId(bindId);
        voucherRise.setVoucherCode(processExecutionContext.execAtScript("VC-@year@month@dayofmonth@sequence('凭证头编号',5,0)"));
        String userNo = rowMap.getString("APPLAY_NAME");
        Date date = rowMap.getDate("APPLAY_DATE");
        voucherRise.setVoucherName(userNo + "的出差申请");
        voucherRise.setVoucherDate(DateUtil.formatdate(date));
        voucherRise.setVoucherText("["+DateUtil.formatdate(date)+"] - " + userNo + "的元出差申请");
        voucherRise.setVoucherType(SDK.getDictAPI().getValue(APPID,  VOUCHER_TYPE_DICT,"0010"));
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
        DBSql.update(connection,"UPDATE BO_EU_TRAVEL SET VOUCHER_TITLE = ?,VOUCHER_DATE = ?,VOUCHER_PAY_NO = ?,VOUCHER_INFO = ?," +
                        "VOUCHER_PAY_STATUS = '1',VOUCHER_PAY_DATE = ?, VOUCHER_REFER_NO = ?,VOUCHER_COMPANY_CODE = ?,VOUCHER_COMPANY_NAME = ? " +
                        "WHERE BINDID = ?",
                new Object[]{
                        voucherRise.getVoucherName(),voucherRise.getVoucherDate(),voucherPayNo,voucherRise.getVoucherText(),
                        DateUtil.formatdate(new Date()),voucherRise.getConsulNo(),voucherRise.getCompanyCode(),voucherRise.getCompanyName(),
                        bindId
                });
        log("凭证信息生成完成");
    }
}
