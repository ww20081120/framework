UPDATE CRON_TRIGGER T
SET T.CRON_EXPRESSION = :trigger.cronExpression
WHERE T.TRIGGER_ID = :trigger.triggerId
