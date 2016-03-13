SELECT MAX(M.SEQ)
  FROM MENU M
 WHERE 1 = 1
   AND PARENT_RESOURCE_ID #if($parentResourceId) = $!parentResourceId #else is null #end