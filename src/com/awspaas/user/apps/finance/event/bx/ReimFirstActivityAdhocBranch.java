package com.awspaas.user.apps.finance.event.bx;

import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.ValueListener;
import com.actionsoft.bpms.commons.database.RowMap;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.exception.BPMNError;
import com.actionsoft.sdk.local.SDK;
import com.actionsoft.sdk.local.api.Logger;
import com.awspaas.user.apps.finance.util.StringUtil;


import java.util.List;

public class ReimFirstActivityAdhocBranch extends ValueListener {
    Logger logger = SDK.getLogAPI().getLogger(this.getClass());

    @Override
    public String getDescription() {
        return "报销金额校验：ERR01，报销金额为错误，请检查";
    }

    @Override
    public String execute(ProcessExecutionContext processExecutionContext) throws Exception {
        String bindId = processExecutionContext.getProcessInstance().getId();
        String boSql = "SELECT REIM_MONEY,APPLAY_NO,IS_PEOJECT_TRAVEL,PROJECT_NO,LOAN_NO,LOAN_MONEY,BALANCE_MONEY,WRITE_OFF_MONEY,REIM_MONEY,REL_TYPE \n" +
                "\t FROM BO_EU_BX WHERE BINDID = ?";
        RowMap rowMap = DBSql.getMap(boSql, bindId);
        if(rowMap != null){
            double reimMoney = rowMap.getDouble("REIM_MONEY");
            log("报销金额 ：" + reimMoney);
            if(reimMoney == 0){
                throw new BPMNError("ERR01","报销金额为错误，请检查");
            }
            double balanceMoney = rowMap.getDouble("BALANCE_MONEY");
            if(balanceMoney <= 0){
                // 本次冲销金额为负数，表示本次报销金额大于借款单中待冲销金额，不予办理
                throw new BPMNError("ERR01","借款单待冲销金额小于本次报销金额，不予办理");
            }
            String applyNo = rowMap.getString("APPLAY_NO");
            String loanNo = rowMap.getString("LOAN_NO");
            String relType = rowMap.getString("REL_TYPE");
            log("申请人 ：" + applyNo + ", 借款单号："+ loanNo + ",核算范围：" + relType);
            if(!StringUtil.isEmpty(relType)){
                if("PROJECT".equalsIgnoreCase(relType)){
                    log("项目核算检查");
                    String isProject = rowMap.getString("IS_PEOJECT_TRAVEL");
                    log("是否项目借款 ：" + isProject);
                    if(StringUtil.isEmpty(isProject) ||  "0".equals(isProject)){
                        // 项目核算，是否项目出差必须勾选
                        throw new BPMNError("ERR02","核算范围为项目，请勾选是否项目借款，并关联项目数据");
                    }else{
                        String projectNo = rowMap.getString("PROJECT_NO");
                        log("项目报销-项目编号：" + projectNo);
                        if(StringUtil.isEmpty(loanNo)){
                            // 查询项目是否有为核销的借款
                            int count = DBSql.getInt("SELECT COUNT(LOAN_NO) FROM BO_EU_LOAN_APPLAY WHERE APPLAY_NO = ? AND ISEND = 1 AND UN_WRITE_OFF_MONEY <> 0 AND APPLAY_PROJECT_NO = ? ",
                                    new Object[]{applyNo,projectNo});
                            log("查询项目中是否有关联的借款单（借款单中未核销金额不为 0） == " + count);
                            if(count != 0){
                                throw new BPMNError("ERR03","选择的项目存在尚未核销的借款单，请优先关联");
                            }
                            // 不存在和项目借款的关联。将前端计算的冲销金额设置为0
                            DBSql.update("UPDATE BO_EU_BX SET WRITE_OFF_MONEY = 0 WHERE BINDID=?",new Object[]{bindId});
                        }else{
                            // 检查借款单的项目是否和选择的项目一致
                            String applyProjectNo = DBSql.getString("SELECT APPLAY_PROJECT_NO FROM BO_EU_LOAN_APPLAY WHERE APPLAY_NO = ? AND ISEND = 1 AND LOAN_NO = ?",
                                    new Object[]{applyNo,loanNo});
                            log("查询借款单关联的项目编号，并和报销借款项目对比 projectNo == appayProjectNo ==》" + projectNo.equals(applyProjectNo));
                            if(!projectNo.equals(applyProjectNo)){
                                throw new BPMNError("ERR04","选择的项目和借款单关联项目不符，请重新关联");
                            }
                            double writeOffMoney = rowMap.getDouble("WRITE_OFF_MONEY");
                            if(balanceMoney < writeOffMoney){
                                throw new BPMNError("ERR03","本次冲销借款单金额大于借款单冲销金额，请取消借款单关联或者减少冲销金额");
                            }
                        }
                    }
                }else if("NORMAL".equalsIgnoreCase(relType)){
                    // 普通流程
                    log("普通核算检查");
                    String isProject = rowMap.getString("IS_PEOJECT_TRAVEL");
                    log("是否项目借款 ：" + isProject);
                    if(!StringUtil.isEmpty(isProject) && "1".equals(isProject)){
                        // 项目核算，是否项目出差必须勾选
                        throw new BPMNError("ERR02","当前核算范围为：" + relType + ",请勿勾选项目出差");
                    }
                    if(!StringUtil.isEmpty(loanNo)){
                        // 普通核算，有关联的借款,判断所选择的借款是否是项目借款
                        String loanSql = "SELECT LOAN_NO FROM BO_EU_LOAN_APPLAY \n" +
                                "\tWHERE APPLAY_NO = ? AND ISEND = 1 AND LOAN_NO = ?  AND (APPLAY_PROJECT_NO IS NULL OR APPLAY_PROJECT_NO = '')";
                        String resLoanNo = DBSql.getString(loanSql, new Object[]{applyNo,loanNo});
                        if(!StringUtil.isEmpty(resLoanNo)){
                            throw new BPMNError("ERR03","当前借款单关联项目，请走项目报销");
                        }
                    }else{
                        DBSql.update("UPDATE BO_EU_BX SET WRITE_OFF_MONEY = 0 WHERE BINDID=?",new Object[]{bindId});
                    }
                }
            }
        }else{
            log("" + rowMap);
            throw new BPMNError("ERR00","系统错误");
        }
        return null;
    }

    private void travelGrid(String bindId,ProcessExecutionContext processExecutionContext) throws Exception{
        DBSql.getMaps("");
    }

    private void log(String s){
        logger.info("ReimFirstActivityAdhocBranch 【报销申请】----" + s);
    }
}
