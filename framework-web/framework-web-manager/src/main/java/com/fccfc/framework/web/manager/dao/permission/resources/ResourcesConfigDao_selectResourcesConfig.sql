SELECT * FROM RESOURCES_CONFIG RC WHERE  1 = 1
#if($moduleCode)
	AND RC.MODULE_CODE=:moduleCode
#end

#if($resourceId)
	AND RESOURCE_ID = :resourceId
#end