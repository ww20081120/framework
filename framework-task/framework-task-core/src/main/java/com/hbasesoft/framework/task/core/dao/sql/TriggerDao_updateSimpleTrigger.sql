UPDATE T_JOB_SIMPLE_TRIGGER T
SET T.BEGIN_TIME = :trigger.beginTime, T.END_TIME = :trigger.endTime, T.TIMES = :trigger.times, T.EXECUTE_INTERVAL = :trigger.executeInterval, T.INTERVAL_UNIT = :trigger.intervalUnit
WHERE T.TRIGGER_ID = :trigger.triggerId
