namespace java com.fccfc.framework.task.api

// task 结构体
struct Task {
1: optional i32 taskId;
2: required string taskName;
3: required string className;
4: required string method;
5: optional string moduleCode;
6: optional i32 priority;
7: optional string isConcurrent;
8: optional string taskState;
9: optional i64 lastExecuteTime;
10: optional i64 nextExcuteDate;
11: optional i32 operatorId;
12: optional i64 createTime;
}

// simpleTrigger 结构体
struct SimpleTrigger {
1: optional i32 triggerId;
2: required string triggerName;
3: optional i64 createTime;
4: optional i32 operatorId;
5: optional i16 triggerType;
6: optional i64 beginTime;
7: optional i64 endTime;
8: optional i32 times;
9: optional i32 executeInterval;
10: optional string intervalUnit;
}

// CronTrigger 结构体
struct CronTrigger {
1: optional i32 triggerId;
2: required string triggerName;
3: optional i64 createTime;
4: optional i32 operatorId;
5: optional i16 triggerType;
6: required string cronExpression;
}

service TaskService {
    void scheduleAllTask();
    void simpleScheduleTask(1:Task task, 2:SimpleTrigger simpleTrigger);
    void cronScheduleTask(1:Task task, 2:CronTrigger cronTrigger);
    void pause(1:Task task);
    void resume(1:Task task);
    void remove(1:Task task);
}