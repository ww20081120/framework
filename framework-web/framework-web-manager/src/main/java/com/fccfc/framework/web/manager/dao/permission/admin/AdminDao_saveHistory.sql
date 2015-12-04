INSERT ADMIN_HISTORY SELECT
    A.ADMIN_ID,
    A.ADMIN_NAME,
    A.OPERATOR_ID,
    A.HEAD_IMG,
    A.GENER,
    A.EMAIL,
    A.PHONE,
    A.ADDRESS,
    A.CREATE_TIME,
    A.STATE,
    A.STATE_DATE,
    :updateDate,
    :operatorId,
    IFNULL(
    (
        SELECT
           MAX(H.SEQ)
        FROM
           ADMIN_HISTORY H
        WHERE
           H.ADMIN_ID = :id
     ),
        0
    	) + 1 AS SEQ
FROM
     ADMIN A
WHERE
    A.ADMIN_ID = :id