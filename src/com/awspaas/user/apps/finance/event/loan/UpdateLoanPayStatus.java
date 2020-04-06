package com.awspaas.user.apps.finance.event.loan;

import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.ExecuteListener;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.emm.util.DateUtil;
import com.actionsoft.sdk.local.SDK;

import java.sql.Timestamp;
import java.time.LocalDateTime;
@Deprecated
public class UpdateLoanPayStatus extends ExecuteListener {

    @Override
    public String getDescription() {
        return "财务支付后更新支付状态";
    }

    @Override
    public void execute(ProcessExecutionContext processExecutionContext) throws Exception {
        SDK.getBOAPI().updateByBindId("BO_EU_LOAN_APPLAY",
                processExecutionContext.getProcessInstance().getId(), "PAY_STATUS", 2);
        String payDate = DateUtil.formatToDate(Timestamp.valueOf(LocalDateTime.now()));
        DBSql.update("UPDATE BO_EU_LOAN_APPLAY SET PAY_STATUS = '2', SET PAY_DATE = "+payDate
                        +" WHERE BINDID="+processExecutionContext.getProcessInstance().getId());
    }
}
