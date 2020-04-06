package com.awspaas.user.apps.finance.util;

import com.actionsoft.exception.BPMNError;

public class PublicExceptionHint {
    public static final String BPMN_ERRORNO = "0312";

    public static String errorMessage(String message) {
        return "系统错误，原因是[" + message + "]";
    }

    public static String unifyThrowErrorMessage(Exception e) {
        e.printStackTrace();
        throw new BPMNError(BPMN_ERRORNO, e.getMessage());
    }
}