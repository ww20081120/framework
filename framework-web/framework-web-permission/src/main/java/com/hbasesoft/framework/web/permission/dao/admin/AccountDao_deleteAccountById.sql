UPDATE T_MANAGER_ACCOUNT 
SET STATE=:state 
WHERE OPERATOR_ID IN (:ids)