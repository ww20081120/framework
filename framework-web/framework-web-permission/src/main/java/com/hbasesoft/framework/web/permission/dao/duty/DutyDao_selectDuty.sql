SELECT D.DUTY_ID,
       D.DUTY_NAME,
       D.ORG_ID,
       D.OPERATOR_ID,
       D.CREATE_TIME,
       D.EXT,
       O.ORG_NAME
  FROM DUTY D, ORG O
  WHERE D.ORG_ID = O.ORG_ID
    AND D.STATE = 'A'
  #if($orgId)
    AND D.ORG_ID = :orgId
  #end
  #if($dutyId)
  	AND D.DUTY_ID = :dutyId
  #end
  ORDER BY D.DUTY_ID ASC