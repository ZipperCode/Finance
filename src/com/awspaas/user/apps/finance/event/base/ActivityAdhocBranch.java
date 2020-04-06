package com.awspaas.user.apps.finance.event.base;

import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.ValueListener;
import com.actionsoft.bpms.commons.database.RowMap;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.exception.BPMNError;
import com.actionsoft.sdk.local.SDK;
import com.actionsoft.sdk.local.api.Logger;
import com.awspaas.user.apps.finance.util.StringUtil;

public abstract class ActivityAdhocBranch extends ValueListener {

    protected String TAG = this.getClass().getSimpleName();

    protected Logger logger = SDK.getLogAPI().getLogger(this.getClass());

    protected void log(String s){
        logger.info(this.getClass().getSimpleName() + " 【"+TAG+"】----" + s);
    }
}
