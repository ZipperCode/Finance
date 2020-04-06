package com.awspaas.user.apps.finance.event.loan;

import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.InterruptListener;
import com.actionsoft.bpms.commons.database.RowMap;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.sdk.local.SDK;
import com.actionsoft.sdk.local.api.Logger;
import com.awspaas.user.apps.finance.constant.FinanceConst;
import com.awspaas.user.apps.finance.util.StringUtil;

public class LoanLastTaskBeforeComplete extends InterruptListener {

    Logger logger = SDK.getLogAPI().getLogger(this.getClass());
    @Override
    public boolean execute(ProcessExecutionContext processExecutionContext) throws Exception {
        String bindId = processExecutionContext.getProcessInstance().getId();
        RowMap rowMap = DBSql.getMap("SELECT IS_LOAN_PROJECT,APPLAY_PROJECT_NO,LOAN_MONEY FROM BO_EU_LOAN_APPLAY WHERE BINDID = ?",bindId);
        if(rowMap != null && !rowMap.isEmpty()){
            // 判断是否项目出差，如果是项目出差，则借款金额应该算在成本中
            String loanProject = rowMap.getString("IS_LOAN_PROJECT");
            if(FinanceConst.YES.equals(loanProject)){
                String projectNo = rowMap.getString("APPLAY_PROJECT_NO");
                double loanMoney = rowMap.getDouble("LOAN_MONEY");
                if(!StringUtil.isEmpty(projectNo)){
                    DBSql.update("UPDATE BO_EU_PROJECT SET PROJECT_COST = PROJECT_COST + ? WHERE PROJECT_NO = ?",new Object[]{loanMoney,projectNo});
                }
            }
        }
        DBSql.update("UPDATE BO_EU_LOAN_APPLAY SET APPLAY_STATUS=? ,PAY_DATE = NOW(),UN_WRITE_OFF_MONEY = LOAN_MONEY WHERE BINDID= ?", new Object[]{"4",bindId});
        return true;
    }

    private void log(String s){
        logger.info("【借款单申请】----》" + s);
    }

    public String getDescription() {
        return "借款申请校验:TASK_BEFORE_COMPLETE 财务通过时校验，通过时-更新流程状态为已支付,更新支付时间为当前时间";
    }
}
