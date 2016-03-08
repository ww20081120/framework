SELECT * FROM T_JOB_TASK T
#if($task)
WHERE 1 = 1
    #if($task.taskName)
        AND T.TASK_NAME = :task.taskName
    #end
    #if($task.moduleCode)
        AND T.MODULE_CODE = :task.moduleCode
    #end
    #if($task.taskState)
        AND T.TASK_STATE = :task.taskState
    #end
    #if($task.taskId)
        AND T.TASK_ID = :task.taskId
    #end
#end