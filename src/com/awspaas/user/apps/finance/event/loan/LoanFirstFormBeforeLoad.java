package com.awspaas.user.apps.finance.event.loan;

import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.ExecuteListener;
import com.actionsoft.bpms.util.DBSql;

@Deprecated
public class LoanFirstFormBeforeLoad extends ExecuteListener {
    @Override
    public void execute(ProcessExecutionContext processExecutionContext) throws Exception {
    }

    @Override
    public String getDescription() {
        return "【借款申请】 ： First 当前创建人是否有尚未结清的借款";
    }
}
