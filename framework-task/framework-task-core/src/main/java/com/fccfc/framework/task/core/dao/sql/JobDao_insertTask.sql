INSERT INTO TASK(TASK_ID, TASK_NAME, CLASS_NAME, METHOD, MODULE_CODE, PRIORITY,IS_CONCURRENT, 
    TASK_STATE, LAST_EXECUTE_TIME, NEXT_EXCUTE_DATE, OPERATOR_ID, CREATE_TIME)
VALUES(:t.taskId, :t.taskName, :t.className, :t.method, :t.moduleCode, :t.priority, :t.isConcurrent, 
    :t.taskState, :t.lastExecuteTime, :t.nextExcuteDate, :t.operatorId, :t.createTime)