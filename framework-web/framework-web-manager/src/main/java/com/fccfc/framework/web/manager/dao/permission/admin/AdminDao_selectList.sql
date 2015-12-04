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
  FROM ADMIN A, OPERATOR O
 WHERE 1 = 1 
 AND A.OPERATOR_ID = O.OPERATOR_ID
 AND A.STATE = 'A'
 AND O.DUTY_ID = :dutyId
 #if($!name)
       AND (ADMIN_NAME LIKE :name OR PHONE LIKE :name OR EMAIL LIKE :name)
 #end