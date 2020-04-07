package com.awspaas.user.apps.finance.event.travel;

import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.commons.database.RowMap;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.sdk.local.SDK;
import com.awspaas.user.apps.finance.event.base.ActivityAdhocBranch;
import com.awspaas.user.apps.finance.util.StringUtil;

import static com.awspaas.user.apps.finance.constant.FinanceConst.*;

public class TravelFirstActivityAdhocBranch extends ActivityAdhocBranch {
    @Override
    public String execute(ProcessExecutionContext processExecutionContext) throws Exception {
        TAG = "【出差申请填写】";
        String bindId = processExecutionContext.getProcessInstance().getId();
        String isProjectTravel = SDK.getBOAPI().getByProcess("BO_EU_TRAVEL",bindId,"IS_PEOJECT_TRAVEL").toString();
        if(StringUtil.isEmpty(isProjectTravel) || "0".equals(isProjectTravel)){
            // 取消关联的出差项目
            DBSql.update("UPDATE BO_EU_TRAVEL SET TRAVEL_PROJECT_NO = '',TRAVEL_PEOJECT = ''");
            //String limitMoneyStr =SDK.getDictAPI().getValue(APPID, BX_MONEY_LIMIT_DICT, "6", "EXTDOUBLE1");
        }
        // 更新流程状态为审批中
        DBSql.update("UPDATE BO_EU_TRAVEL SET STATUS = ? WHERE BINDID=?",new Object[]{APPROVAL,bindId});
        return null;
    }
}
