SELECT COUNT(F.FUNCTION_ID)
FROM FUNCTION F
WHERE 1 = 1
#if($directoryCode)
AND F.DIRECTORY_CODE = :directoryCode
#end
#if($functionName && $functionName !='' )
AND F.FUNCTION_NAME LIKE CONCAT('%',:functionName,'%')
#end