SELECT MAX(M.SEQ)
  FROM T_MANAGER_MENU M
 WHERE 1 = 1
   AND PARENT_RESOURCE_ID #if($parentResourceId) = $!parentResourceId #else is null #end