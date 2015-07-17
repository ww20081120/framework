SELECT * FROM OPERATOR O
WHERE 1 = 1
#if($id)
AND O.OPERATOR_ID = :id
#end
#if($code)
AND O.OPERATOR_CODE = :code
#end