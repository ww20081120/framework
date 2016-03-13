SELECT *
  FROM MODULE A 
#if($pojo)
 WHERE 1 = 1
 	#if($pojo.moduleCode)
   		AND A.MODULE_CODE = :pojo.moduleCode
   	#end
#end   	