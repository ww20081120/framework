DELETE FROM T_MANAGER_ROLE_RESOURCE
WHERE 
#if($roleId)
ROLE_ID = :roleId 
#end
#if($roleIds)
ROLE_ID IN :roleIds
#end
#if($resourceType)
AND RESOURCE_TYPE = :resourceType
#end