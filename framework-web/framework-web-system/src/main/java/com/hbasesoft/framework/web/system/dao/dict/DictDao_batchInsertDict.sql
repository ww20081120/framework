INSERT INTO DICTIONARY
(
    DICT_CODE,
    DICT_NAME,
    REMARK
)
VALUES
#foreach ($pojo in $pojoList)
    #if($velocityCount != 1),#end
    (
        '$pojo.dictCode',
        '$pojo.dictName',
        '$pojo.remark'
    )
#end
