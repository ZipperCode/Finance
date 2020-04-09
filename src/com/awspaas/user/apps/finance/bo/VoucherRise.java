package com.awspaas.user.apps.finance.bo;

import java.util.Date;

public class VoucherRise {
    String bindId;
    String voucherCode;
    String voucherName;
    String voucherText;
    String companyCode;
    String companyName;
    String voucherDate;
    String postDate;
    String consulNo;
    String voucherType;
    String accountYear;
    String currencyCode;
    String busDocType;
    String bussSys;
    String refCode;
    String transDate;
    String acDocNo;

    public String getBindId() {
        return bindId;
    }

    public void setBindId(String bindId) {
        this.bindId = bindId;
    }

    public String getAcDocNo() {
        return acDocNo;
    }

    public void setAcDocNo(String acDocNo) {
        this.acDocNo = acDocNo;
    }

    public String getVoucherCode() {
        return voucherCode;
    }

    public void setVoucherCode(String voucherCode) {
        this.voucherCode = voucherCode;
    }

    public String getVoucherName() {
        return voucherName;
    }

    public void setVoucherName(String voucherName) {
        this.voucherName = voucherName;
    }

    public String getVoucherText() {
        return voucherText;
    }

    public void setVoucherText(String voucherText) {
        this.voucherText = voucherText;
    }

    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getVoucherDate() {
        return voucherDate;
    }

    public void setVoucherDate(String voucherDate) {
        this.voucherDate = voucherDate;
    }

    public String getPostDate() {
        return postDate;
    }

    public void setPostDate(String postDate) {
        this.postDate = postDate;
    }

    public String getConsulNo() {
        return consulNo;
    }

    public void setConsulNo(String consulNo) {
        this.consulNo = consulNo;
    }

    public String getVoucherType() {
        return voucherType;
    }

    public void setVoucherType(String voucherType) {
        this.voucherType = voucherType;
    }

    public String getAccountYear() {
        return accountYear;
    }

    public void setAccountYear(String accountYear) {
        this.accountYear = accountYear;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getBusDocType() {
        return busDocType;
    }

    public void setBusDocType(String busDocType) {
        this.busDocType = busDocType;
    }

    public String getBussSys() {
        return bussSys;
    }

    public void setBussSys(String bussSys) {
        this.bussSys = bussSys;
    }

    public String getRefCode() {
        return refCode;
    }

    public void setRefCode(String refCode) {
        this.refCode = refCode;
    }

    public String getTransDate() {
        return transDate;
    }

    public void setTransDate(String transDate) {
        this.transDate = transDate;
    }

    @Override
    public String toString() {
        return "VoucherRise{" +
                "bindId='" + bindId + '\'' +
                ", voucherCode='" + voucherCode + '\'' +
                ", voucherName='" + voucherName + '\'' +
                ", voucherText='" + voucherText + '\'' +
                ", companyCode='" + companyCode + '\'' +
                ", companyName='" + companyName + '\'' +
                ", voucherDate='" + voucherDate + '\'' +
                ", postDate='" + postDate + '\'' +
                ", consulNo='" + consulNo + '\'' +
                ", voucherType='" + voucherType + '\'' +
                ", accountYear='" + accountYear + '\'' +
                ", currencyCode='" + currencyCode + '\'' +
                ", busDocType='" + busDocType + '\'' +
                ", bussSys='" + bussSys + '\'' +
                ", refCode='" + refCode + '\'' +
                ", transDate='" + transDate + '\'' +
                ", acDocNo='" + acDocNo + '\'' +
                '}';
    }
}