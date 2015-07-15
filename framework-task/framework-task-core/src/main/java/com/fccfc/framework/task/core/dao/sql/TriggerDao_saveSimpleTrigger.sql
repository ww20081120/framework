INSERT INTO 
    SIMPLE_TRIGGER (TRIGGER_ID, TRIGGER_NAME, BEGIN_TIME, END_TIME, TIMES, EXECUTE_INTERVAL, INTERVAL_UNIT, CREATE_TIME, OPERATOR_ID)
VALUES
    (:t.triggerId, :t.tName, :t.beginTime, :t.endTime, :t.times, :t.executeInterval, :t.intervalUnit, :t.createTime, :t.operatorId)