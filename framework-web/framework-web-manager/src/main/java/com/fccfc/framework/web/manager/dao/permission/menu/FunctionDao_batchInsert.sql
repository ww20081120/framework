insert into FUNCTION
(
   FUNCTION_ID
  ,DIRECTORY_CODE
  ,FUNCTION_NAME
  ,CREATE_TIME
  ,REMARK
)
VALUES
#foreach ($pojo in $pojoList)
  #if($velocityCount != 1),#end
  (
		 $pojo.functionId
		,'$pojo.directoryCode'
		,'$pojo.functionName'
		,CURRENT_TIMESTAMP
		,'$pojo.remark'
	)
#end