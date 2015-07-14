#if($trigger.triggerId)
UPDATE SIMPLE_TRIGGER T
SET T.BEGIN_TIME = :trigger.beginTime, T.END_TIME = :trigger.endTime, T.TIMES = :trigger.times, T.EXECUTE_INTERVAL = :trigger.executeInterval, T.INTERVAL_UNIT = :trigger.intervalUnit
WHERE T.TRIGGER_ID = :trigger.triggerId
#else
INSERT INTO 
    SIMPLE_TRIGGER (TRIGGER_NAME, BEGIN_TIME, END_TIME, TIMES, EXECUTE_INTERVAL, INTERVAL_UNIT, CREATE_TIME, OPERATOR_ID)
VALUES
    (:trigger.triggerName, :trigger.beginTime, :trigger.endTime, :trigger.times, :trigger.executeInterval, :trigger.intervalUnit, :trigger.createTime, :trigger.operatorId)
#end