DELETE FROM FUNCTION
WHERE 
#if($functionId)
FUNCTION_ID = :functionId 
#end
#if($functionIds)
FUNCTION_ID IN :functionIds
#end