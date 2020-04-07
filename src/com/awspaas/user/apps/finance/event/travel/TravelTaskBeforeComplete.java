package com.awspaas.user.apps.finance.event.travel;

import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.util.DBSql;
import com.awspaas.user.apps.finance.event.base.TaskBeforeComplete;

import java.sql.Connection;

import static com.awspaas.user.apps.finance.constant.FinanceConst.HAS_AGREE;

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

    private void buildVoucher(Connection connection,ProcessExecutionContext processExecutionContext){
        log("开始生成凭证信息");

        log("凭证信息生成完成");
    }
}
