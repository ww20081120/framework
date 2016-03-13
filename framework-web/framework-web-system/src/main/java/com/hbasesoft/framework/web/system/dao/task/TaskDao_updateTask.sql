UPDATE TASK
   SET TASK_NAME         = :pojo.taskName,
       CLASS_NAME        = :pojo.className,
       METHOD            = :pojo.method,
       MODULE_CODE       = :pojo.moduleCode,
       PRIORITY          = :pojo.priority,
       IS_CONCURRENT     = :pojo.isConcurrent,
       TASK_STATE        = :pojo.taskState,
       LAST_EXECUTE_TIME = :pojo.lastExecuteTime,
       NEXT_EXCUTE_DATE  = :pojo.nextExcuteDate,
       OPERATOR_ID       = :pojo.operatorId,
       CREATE_TIME       = :pojo.createTime
 WHERE TASK_ID = :pojo.taskId