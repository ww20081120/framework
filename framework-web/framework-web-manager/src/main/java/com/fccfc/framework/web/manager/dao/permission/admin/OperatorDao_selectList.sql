SELECT O.OPERATOR_ID,
       O.OPERATOR_TYPE,
       O.OPERATOR_CODE,
       O.DUTY_ID,
       O.USER_NAME,
       O.PASSWORD,
       O.CREATE_DATE,
       O.STATE,
       O.STATE_DATE,
       O.IS_LOCKED,
       O.PWD_EXP_DATE,
       O.REGIST_IP,
       O.LAST_IP,
       O.LAST_LOGIN_DATE,
       O.LOGIN_FAIL
  FROM OPERATOR O
 WHERE 1 = 1
  #if($dutyId)
   AND O.DUTY_ID = :dutyId
  #end
ORDER BY O.OPERATOR_ID ASC