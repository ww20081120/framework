INSERT INTO DICTIONARY_DATA
(
    DICT_DATA_ID,
    DICT_CODE,
    DICT_DATA_NAME,
    DICT_DATA_VALUE,
    IS_FIXED,
    IS_CANCEL
)
VALUES
#foreach ($pojo in $pojoList)
    #if($velocityCount != 1),#end
    (
        $pojo.dictDataId,
        '$pojo.dictCode',
        '$pojo.dictDataName',
        '$pojo.dictDataValue',
        '$pojo.isFixed',
        '$pojo.isCancel'
    )
#end
