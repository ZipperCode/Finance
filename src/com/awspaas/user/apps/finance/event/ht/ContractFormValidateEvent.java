package com.awspaas.user.apps.finance.event.ht;

import com.actionsoft.bpms.bo.engine.BO;
import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.InterruptListener;
import com.actionsoft.bpms.bpmn.engine.listener.ListenerConst;
import com.actionsoft.bpms.commons.database.RowMapper;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.emm.util.DateUtil;
import com.actionsoft.exception.BPMNError;
import com.actionsoft.sdk.local.SDK;
import com.actionsoft.sdk.local.api.LogAPI;
import com.google.zxing.common.StringUtils;
import io.netty.util.internal.StringUtil;
import net.sf.cglib.core.Local;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;

public class ContractFormValidateEvent extends InterruptListener {

    public String getDescription() {
        return "合同-表单校验" +
                "ERR01.当前[合同名称]已存在，请检查 \r\n" +
                "ERR02.合同开始日期不能大于合同结束日期\r\b" +
                "ERR02.合同日期时间差错误\r\n" +
                "ERR03.办理前效验‘项目名称’不能重复\r\n";
    }

    @Override
    public boolean execute(ProcessExecutionContext processExecutionContext) throws Exception {
        String bindId = processExecutionContext.getProcessInstance().getId();
        //BO bo = SDK.getBOAPI().getByProcess("BO_EU_PROJECT", processExecutionContext.getProcessInstance().getId());
        BO formData = (BO) processExecutionContext.getParameter(ListenerConst.FORM_EVENT_PARAM_FORMDATA);
        String contractName = formData.get("CONTRACTNAME", String.class);
        String contractNameSql = "SELECT COUNT(CONTRACTNAME) FROM BO_EU_LC_CONTRACTTURNKEY WHERE BINDID=?";
        if(contractName != null && DBSql.getInt(contractNameSql,new Object[]{bindId})>0){
            throw new BPMNError("ERR01", "当前[合同名称]已存在，请检查");
        }

        return true;
    }



}
