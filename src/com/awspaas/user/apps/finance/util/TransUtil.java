package com.awspaas.user.apps.finance.util;

import com.awspaas.user.apps.finance.bo.VoucherRise;

import java.util.HashMap;
import java.util.Map;

public class TransUtil {

    public static Map<String,Object> voucher2Map(VoucherRise voucherRise){
        Map<String,Object> map = new HashMap<>();
        map.put("VOUCHERCODE",voucherRise.getVoucherCode());
        map.put("VOUCHERNAME",voucherRise.getVoucherName());
        map.put("VOUCHERTEXT",voucherRise.getVoucherText());
        map.put("COMPANYCODE",voucherRise.getCompanyCode());
        map.put("COMPANYNAME",voucherRise.getCompanyName());
        map.put("ACCOUNTYEAR",voucherRise.getAccountYear());
        map.put("VOUCHERDATE",voucherRise.getVoucherDate());
        map.put("POSTDATE",voucherRise.getPostDate());
        map.put("CONSULTNO",voucherRise.getConsulNo());
        map.put("VOUCHERTYPE",voucherRise.getVoucherType());
        map.put("CURRENCYCODE",voucherRise.getCurrencyCode());
        map.put("BUSS_SYS",voucherRise.getBussSys());
        map.put("BUSS_DOC_TYPE",voucherRise.getBusDocType());
        map.put("AC_DOC_NO",voucherRise.getAcDocNo());
        map.put("TRANS_DATE",voucherRise.getTransDate());
        map.put("REF_DOC_NO",voucherRise.getRefCode());
        map.put("BINDID",voucherRise.getBindId());
        return map;
    }
}
