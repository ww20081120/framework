INSERT INTO 
    T_JOB_CRON_TRIGGER (TRIGGER_ID, TRIGGER_NAME, CRON_EXPRESSION, CREATE_TIME, OPERATOR_ID)
VALUES
    (:t.triggerId, :t.triggerName, :t.cronExpression, :t.createTime, :t.operatorId)