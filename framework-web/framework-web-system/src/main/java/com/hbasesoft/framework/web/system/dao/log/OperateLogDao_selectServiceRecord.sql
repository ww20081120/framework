SELECT A.OPERATE_LOG_ID,A.EVENT_ID,A.IP,str_to_date(A.CREATE_TIME ,'%Y-%m-%d') CREATE_TIME,A.OPERATOR_ID,A.PARAMS_VALUE,B.EVENT_NAME,C.USER_NAME
FROM OPERATE_LOG A, EVENT B, OPERATOR C
WHERE A.OPERATOR_ID = C.OPERATOR_ID
AND A.EVENT_ID = B.EVENT_ID
AND A.OPERATOR_ID = :operatorid
AND A.CREATE_TIME >= str_to_date(:startdate ,'%Y-%m-%d')
AND A.CREATE_TIME <= str_to_date(:enddate,'%Y-%m-%d') +
interval '1' day - interval '1' second
#if($eventid != 0)
AND A.EVENT_ID = :eventid
#end
