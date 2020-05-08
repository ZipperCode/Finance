package com.awspaas.user.apps.finance.event.ht;

import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.exception.BPMNError;
import com.actionsoft.sdk.local.SDK;
import com.awspaas.user.apps.finance.event.base.TaskBeforeComplete;
import com.awspaas.user.apps.finance.util.StringUtil;

import java.sql.Connection;

public class ContractTaskBeforeComplete extends TaskBeforeComplete {
    @Override
    public boolean execute(ProcessExecutionContext processExecutionContext) throws Exception {
        TAG = "【合同审核】";
        String bindId = processExecutionContext.getProcessInstance().getId();
        Connection connection = DBSql.open();
        try{
            connection.setAutoCommit(false);
            // 获取甲方编码
            String contractFistPartyNo = SDK.getBOAPI().getByProcess("BO_EU_LC_CONTRACTTURNKEY", bindId,"CONTRACTFISTPARTYNO").toString();
            log("甲方合同编码 ： " + contractFistPartyNo);
            if(!StringUtil.isEmpty(contractFistPartyNo)){
                int count = DBSql.getInt("SELECT COUNT(CUSTOMER_NO) FROM BO_EU_CUSTOMER WHERE CUSTOMER_NO = ?",new Object[]{contractFistPartyNo});
                log("查询客户信息记录 ： count = " + count);
                if(count != 0){
                    log("关联客户签约状态");
                    DBSql.update("UPDATE BO_EU_CUSTOMER SET IS_SIGNING = '1' WHERE CUSTOMER_NO = ?",new Object[]{contractFistPartyNo});
                }
            }
            String projects = SDK.getBOAPI().getByProcess("BO_EU_LC_CONTRACTTURNKEY",bindId,"PROJECTAPPROVALNO").toString();
            log("获取项目关联信息 ：" + projects);
            String[] projectNos = projects.split(",");
            String updateProjectRefer = "UPDATE BO_EU_PROJECT SET IS_USE = 1 WHERE PROJECT_NO IN("+fill(projectNos.length)+")";
            log("更新项目与合同关联状态 ：SQL ===》"+updateProjectRefer);
            int update = DBSql.update(updateProjectRefer, projectNos);
            if(update != projectNos.length){
                throw new BPMNError("ERR03", "更新Project记录 ：" + update + "条,实际记录 ：" + projectNos.length +"条");
            }
            connection.commit();
        }catch (Exception e){
            connection.rollback();
            e.printStackTrace();
            return false;
        }
        return  true;
    }

    private static String fill(int length){
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < length; i++) {
            stringBuffer.append("?").append(",");
        }
        return stringBuffer.substring(0,stringBuffer.length()-1);
    }

    @Override
    public String getDescription() {
        return "【合同-财务审核】 如果甲方在客户信息中，则将客户信息更改为已签约";
    }
}
