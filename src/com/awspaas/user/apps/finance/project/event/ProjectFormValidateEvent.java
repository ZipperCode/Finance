package com.awspaas.user.apps.finance.project.event;

import com.actionsoft.bpms.bo.engine.BO;
import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.InterruptListener;
import com.actionsoft.bpms.bpmn.engine.listener.ListenerConst;
import com.actionsoft.bpms.commons.database.RowCallbackHandler;
import com.actionsoft.bpms.commons.database.RowMapper;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.emm.util.DateUtil;
import com.actionsoft.exception.BPMNError;
import com.actionsoft.sdk.local.SDK;
import com.actionsoft.sdk.local.api.LogAPI;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ProjectFormValidateEvent extends InterruptListener {

    public String getDescription() {
        return "项目信息表-表单校验" +
                "ERR01.计划开始时间不能小于当前日期 \r\n" +
                "ERR01.计划结束日期不能小于计划开始日期 \r\n" +
                "ERR02.项目开始时间不能小于当前时间\r\b" +
                "ERR02.项目结束时间不能小于项目开始时间\r\n" +
                "ERR03.办理前效验‘项目名称’不能重复\r\n" +
                "ERR04.子表项目人员必须有一个";
    }

    @Override
    public boolean execute(ProcessExecutionContext processExecutionContext) throws Exception {
        BO bo = SDK.getBOAPI().getByProcess("BO_EU_PROJECT", processExecutionContext.getProcessInstance().getId());
        Timestamp projectPlanStartDate = bo.get("PROJECT_PLAN_START_DATE", Timestamp.class);
        Timestamp projectPlanEndDate = bo.get("PROJECT_PLAN_END_DATE", Timestamp.class);
        LogAPI.getLogger(this.getClass()).info("计划开始时间为："+
                DateUtil.formatToDate(projectPlanStartDate) + " -- 结束时间为："+DateUtil.formatToDate(projectPlanEndDate));
        if(projectPlanStartDate != null && projectPlanEndDate != null){
            if(projectPlanStartDate.toLocalDateTime().toLocalDate().isAfter(LocalDate.now())){
                throw new BPMNError("ERROR1","计划开始时间不能小于等于当前日期");
            }
            if(projectPlanStartDate.toLocalDateTime().toLocalDate().isAfter(projectPlanEndDate.toLocalDateTime().toLocalDate())) {
                throw new BPMNError("ERROR1", "计划结束日期不能小于计划开始日期");
            }
        }

        Timestamp projectStartDate = bo.get("PROJECT_START_DATE", Timestamp.class);
        Timestamp projectEndDate = bo.get("PROJECT_END_DATE", Timestamp.class);
        LogAPI.getLogger(this.getClass()).info("开始时间为："+
                DateUtil.formatToDate(projectStartDate) + " -- 结束时间为："+DateUtil.formatToDate(projectEndDate));
        if(projectStartDate != null && projectEndDate != null){
            if(projectStartDate.toLocalDateTime().toLocalDate().isAfter(LocalDate.now())){
                throw new BPMNError("ERROR1","项目开始时间不能小于等于当前时间");
            }
            if(projectStartDate.toLocalDateTime().toLocalDate().isAfter(projectEndDate.toLocalDateTime().toLocalDate())) {
                throw new BPMNError("ERROR1", "项目结束时间不能小于项目开始时间");
            }
        }

        String bindId = processExecutionContext.getProcessInstance().getId();
        String projectNameCountSql = "SELECT COUNT(PROJECT_NAME) FROM `BO_EU_PROJECT`";
        String projectDetailCountSql = "SELECT COUNT(*) FROM `BO_EU_PROJECT_TEAM_DETAIL` WHERE BINDID="+bindId;
        String notNullUserNameSql = "SELECT USERNAME,IS_LEADER FROM BO_EU_PROJECT_DETAIL WHERE BINDID=? ORDER BY IS_LEADER ASC LIMIT0,5 ";
        if(DBSql.getInt(projectNameCountSql,new Object[0])>0){
            throw new BPMNError("ERR03", "当前[客户名称]已存在，请确认");
        }else if(DBSql.getInt(projectDetailCountSql,new Object[0])>0){
            throw new BPMNError("ERR04", "项目团队人员信息不能为空");
        }
        class DetailBean{
            String username;
            int isLeader;
        }
        List<DetailBean> detailBeanList = DBSql.query(notNullUserNameSql, new RowMapper<DetailBean>() {
            @Override
            public DetailBean mapRow(ResultSet resultSet, int i) throws SQLException {
                DetailBean detailBean = new DetailBean();
                detailBean.username = resultSet.getString(0);
                detailBean.isLeader = resultSet.getInt(1);
                return detailBean;
            }
        }, bindId);

        if(detailBeanList == null || detailBeanList.size() == 0){
            throw new BPMNError("ERR05","项目最少有一人");
        }

        long count = detailBeanList.stream().filter(e -> e.isLeader == 1).count();
        if(count > 1){
            throw new BPMNError("ERR05","部门只能有一个领导者");
        }

        // 判断是否存在多个领导者领导者
        //BO formData = (BO) processExecutionContext.getParameter(ListenerConst.FORM_EVENT_PARAM_FORMDATA);
//        List<BO> gridData = (List) processExecutionContext.getParameter(ListenerConst.FORM_EVENT_PARAM_GRIDDATA);
//        // 更新流程状态为审核中
//        SDK.getBOAPI().updateByBindId("BO_EU_PROJECT",
//                processExecutionContext.getProcessInstance().getId(), "STATUS", 1);
        return true;
    }
}
