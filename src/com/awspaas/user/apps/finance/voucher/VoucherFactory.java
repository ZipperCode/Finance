package com.awspaas.user.apps.finance.voucher;

import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.bpms.util.UUIDGener;
import com.actionsoft.sdk.local.SDK;
import com.awspaas.user.apps.finance.util.DateUtil;

import java.awt.image.ImageProducer;
import java.sql.Connection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class VoucherFactory {

    public static Map<String,Object> buildVoucher(Map<String,Object> param){
        Map<String,Object> voucher = new HashMap<>();

        return voucher;
    }

    public static void insertVoucher(){

    }

    public static void insertVoucherRise(Map<String,Object> param){
        DBSql.update("INSERT INTO BO_EU_VOUCHER_RISE \n" +
                "( VOUCHERCODE,VOUCHERNAME,VOUCHERTEXT,COMPANYCODE,COMPANYNAME,ACCOUNTYEAR,VOUCHERDATE,POSTDATE,CONSULTNO,VOUCHERTYPE,CURRENCYCODE,\n" +
                "BUSS_SYS,BUSS_DOC_TYPE,AC_DOC_NO,TRANS_DATE,REF_DOC_NO\n" +
                ") VALUES( :VOUCHERCODE,:VOUCHERNAME,:VOUCHERTEXT,:COMPANYCODE,:COMPANYNAME,:ACCOUNTYEAR,:VOUCHERDATE,:POSTDATE,:CONSULTNO,:VOUCHERTYPE,:CURRENCYCODE," +
                " :BUSS_SYS,:BUSS_DOC_TYPE,:AC_DOC_NO,:TRANS_DATE,:REF_DOC_NO)", param);
    }

    public static void insertVoucherRise(Connection connection,Map<String, Object> param){
        System.out.println("插入凭证头信息 ==》" + param);
        param.put("ID", UUIDGener.getUUID());
        param.put("ORGID","8911e732-b42a-4556-853f-ad32761bcbee");
        param.put("CREATEDATE", new Date());
        param.put("ISEND",1);
        DBSql.update(connection,"INSERT INTO BO_EU_VOUCHER_RISE \n" +
                "( VOUCHERCODE,VOUCHERNAME,VOUCHERTEXT,COMPANYCODE,COMPANYNAME,ACCOUNTYEAR,VOUCHERDATE,POSTDATE,CONSULTNO,VOUCHERTYPE,CURRENCYCODE,\n" +
                "BUSS_SYS,BUSS_DOC_TYPE,AC_DOC_NO,TRANS_DATE,REF_DOC_NO,ID,ORGID,BINDID,CREATEDATE,ISEND\n" +
                ") VALUES( :VOUCHERCODE,:VOUCHERNAME,:VOUCHERTEXT,:COMPANYCODE,:COMPANYNAME,:ACCOUNTYEAR,:VOUCHERDATE,:POSTDATE,:CONSULTNO,:VOUCHERTYPE,:CURRENCYCODE," +
                " :BUSS_SYS,:BUSS_DOC_TYPE,:AC_DOC_NO,:TRANS_DATE,:REF_DOC_NO,:ID,:ORGID,:BINDID,:CREATEDATE,:ISEND)", param);
    }

}
