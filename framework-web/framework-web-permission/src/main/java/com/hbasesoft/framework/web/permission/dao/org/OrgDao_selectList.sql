SELECT O.ORG_ID,
       O.ORG_NAME,
       O.ORG_CODE,
       O.PARENT_ORG_ID,
       O.OWNER_AREA,
       O.CREATE_TIME,
       O.OPERATOR_ID,
       O.EXT
  FROM T_MANAGER_ORG O
  WHERE O.STATE = 'A'
  #if($parentOrgId)
    AND O.PARENT_ORG_ID = :parentOrgId
  #end
  ORDER BY O.ORG_ID ASC