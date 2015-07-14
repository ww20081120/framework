#if($trigger.triggerId)
UPDATE CRON_TRIGGER T
SET T.CRON_EXPRESSION = :trigger.cronExpression
WHERE T.TRIGGER_ID = :trigger.triggerId
#else
INSERT INTO 
    CRON_TRIGGER (TRIGGER_NAME, CRON_EXPRESSION, CREATE_TIME, OPERATOR_ID)
VALUES
    (:trigger.triggerName, :trigger.cronExpression, :trigger.createTime, :trigger.operatorId)
#end