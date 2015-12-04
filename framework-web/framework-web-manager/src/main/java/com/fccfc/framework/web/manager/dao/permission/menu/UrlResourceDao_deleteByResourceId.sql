DELETE FROM URL_RESOURCE
WHERE 
#if($resourceId)
RESOURCE_ID = :resourceId 
#end
#if($resourceIds)
RESOURCE_ID IN :resourceIds
#end