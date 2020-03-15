package com.awspaas.user.apps.finance.project.event;

import com.actionsoft.bpms.bo.engine.BO;
import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.InterruptListener;
import com.actionsoft.emm.util.DateUtil;
import com.actionsoft.exception.BPMNError;
import com.actionsoft.sdk.local.SDK;
import com.actionsoft.sdk.local.api.LogAPI;

import java.sql.Timestamp;
import java.util.Date;
@Deprecated
public class ProjectDateValidationEvent extends InterruptListener {

    public String getDescription() {
        return "关键项业务校验。ERR01.计划开始时间不能小于当前日期;ERR01.计划结束日期不能小于计划开始日期；" +
                "ERR02.项目开始时间不能小于当前时间；ERR02.项目结束时间不能小于项目开始时间";
    }

    @Override
    public boolean execute(ProcessExecutionContext ctx) throws Exception {
        BO bo = SDK.getBOAPI().getByProcess("BO_EU_PROJECT", ctx.getProcessInstance().getId());

        Timestamp projectPlanStartDate = bo.get("PROJECT_PLAN_START_DATE", Timestamp.class);
        Timestamp projectPlanEndDate = bo.get("PROJECT_PLAN_END_DATE", Timestamp.class);
        LogAPI.getLogger(this.getClass()).info("计划开始时间为："+
                DateUtil.formatToDate(projectPlanStartDate) + " -- 结束时间为："+DateUtil.formatToDate(projectPlanEndDate));
        if(projectPlanStartDate != null && projectPlanEndDate != null){
            Date now = new Date();
            if(projectPlanStartDate.getTime() < now.getTime()){
                throw new BPMNError("ERROR1","计划开始时间不能小于等于当前日期");
            }
            if(projectPlanStartDate.getTime() > projectPlanEndDate.getTime()) {
                throw new BPMNError("ERROR1", "计划结束日期不能小于计划开始日期");
            }
        }

        Timestamp projectStartDate = bo.get("PROJECT_START_DATE", Timestamp.class);
        Timestamp projectEndDate = bo.get("PROJECT_END_DATE", Timestamp.class);
        LogAPI.getLogger(this.getClass()).info("开始时间为："+
                DateUtil.formatToDate(projectStartDate) + " -- 结束时间为："+DateUtil.formatToDate(projectEndDate));
        if(projectStartDate != null && projectEndDate != null){
            if(projectStartDate.getTime() < new Date().getTime()){
                throw new BPMNError("ERROR1","项目开始时间不能小于等于当前时间");
            }
            if(projectStartDate.getTime() > projectEndDate.getTime()) {
                throw new BPMNError("ERROR1", "项目结束时间不能小于项目开始时间");
            }
        }



        return true;
    }
}
