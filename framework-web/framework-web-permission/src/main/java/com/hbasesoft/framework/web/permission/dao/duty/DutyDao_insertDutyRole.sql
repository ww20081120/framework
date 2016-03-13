INSERT INTO DUTY_ROLE
(DUTY_ID, ROLE_ID)
VALUES
#foreach ($roleId in $roleIds)
  #if($velocityCount != 1),#end
  (:dutyId, $roleId)
#end