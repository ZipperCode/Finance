package com.awspaas.user.apps.finance.util;

import com.actionsoft.bpms.commons.database.ColumnRowMapRowMapper;
import com.actionsoft.bpms.commons.database.RowMap;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.sdk.local.SDK;
import java.util.List;

public class SendSystemMessage {
    public static void sendMessage(String sender, String bindid, String content) {
        List<RowMap> list = DBSql.query("SELECT DISTINCT TARGET TARGET FROM  WFH_TASK WHERE PROCESSINSTID='" + bindid + "'", new ColumnRowMapRowMapper(), new Object[0]);
        for (int i = 0; i < list.size(); i++) {
            sendSystemMessage(sender, list.get(i).getString("TARGET"), content);
        }
    }

    public static boolean sendSystemMessage(String sender, String receiver, String content) {
        try {
            return SDK.getNotificationAPI().sendMessage(sender, receiver, content);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}