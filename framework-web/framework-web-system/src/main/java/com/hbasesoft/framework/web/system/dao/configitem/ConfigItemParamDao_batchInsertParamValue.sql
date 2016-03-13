INSERT INTO CONFIG_ITEM_PARAM_VALUE
(
    CONFIG_ITEM_ID,
    PARAM_CODE,
    PARAM_VALUE_ID,
    VALUE_MARK,
    VALUE,
    REMARK
)
VALUES
#foreach($pojo in $pojoList)
    #if($velocityCount != 1) , #end
    (
        $pojo.configItemId,
        '$pojo.paramCode',
        $pojo.paramValueId,
        '$pojo.valueMark',
        '$!pojo.value',
        '$!pojo.remark'
    )
#end