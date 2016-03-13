SELECT A.ADMIN_ID,
       A.ADMIN_NAME,
       A.CREATE_TIME,
       A.STATE,
       A.STATE_DATE,
       A.EMAIL,
       A.PHONE,
       A.OPERATOR_ID,
       A.ADDRESS,
       A.HEAD_IMG,
       A.GENER
  FROM ADMIN A, OPERATOR O, DUTY D
 WHERE A.OPERATOR_ID = O.OPERATOR_ID
 AND O.DUTY_ID = D.DUTY_ID
 AND A.STATE = 'A'
 #if($orgId)
 AND D.ORG_ID = :orgId
 #end
 #if($dutyId)
 AND O.DUTY_ID = :dutyId
 #end
 #if($!name)
       AND (ADMIN_NAME LIKE :name OR PHONE LIKE :name OR EMAIL LIKE :name)
 #end