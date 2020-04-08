package com.awspaas.user.apps.finance.event.ht;

import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.sdk.local.SDK;
import com.awspaas.user.apps.finance.event.base.TaskBeforeComplete;
import com.awspaas.user.apps.finance.util.StringUtil;

public class ContractTaskBeforeComplete extends TaskBeforeComplete {
    @Override
    public boolean execute(ProcessExecutionContext processExecutionContext) throws Exception {
        TAG = "【合同审核】";
        String bindId = processExecutionContext.getProcessInstance().getId();
        // 获取甲方编码
        String contractFistPartyNo = SDK.getBOAPI().getByProcess("BO_EU_LC_CONTRACTTURNKEY", bindId,"CONTRACTFISTPARTYNO").toString();
        if(!StringUtil.isEmpty(contractFistPartyNo)){
            int count = DBSql.getInt("SELECT COUNT(CUSTOMER_NO) FROM BO_EU_CUSTOMER WHERE CUSTOMER_NO = ?",new Object[]{contractFistPartyNo});
            if(count != 0){
                DBSql.update("UPDATE BO_EU_CUSTOMER SET IS_SIGNING = '1'");
            }
        }
        return  true;
    }

    @Override
    public String getDescription() {
        return "【合同-财务审核】 如果甲方在客户信息中，则将客户信息更改为已签约";
    }
}
