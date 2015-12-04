INSERT INTO CONFIG_ITEM_PARAM_HISTORY
(
    CONFIG_ITEM_ID,
    PARAM_CODE,
    SEQ,
    PARAM_NAME,
    PARAM_VALUE,
    DEFAULT_PARAM_VALUE,
    DATA_TYPE,
    INPUT_TYPE,
    VALUE_SCRIPT,
    UPDATE_TIME,
    REMARK,
    OPERATOR_ID,
    CHANNEL_ID
)
VALUES
#foreach($pojo in $pojoList)
    #if($velocityCount != 1) , #end
(
    $pojo.configItemId,
    '$pojo.paramCode',
    $pojo.seq,
    '$pojo.paramName',
    '$!pojo.paramValue',
    '$!pojo.defaultParamValue',
    '$!pojo.dataType',
    '$!pojo.inputType',
    '$!pojo.valueScript',
    CURRENT_TIMESTAMP(),
    '$!pojo.remark',
    $!pojo.operatorId,
    $!pojo.channelId
)
#end