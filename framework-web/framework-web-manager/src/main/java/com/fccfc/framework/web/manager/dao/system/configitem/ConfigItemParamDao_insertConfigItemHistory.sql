INSERT INTO CONFIG_ITEM_HISTORY
(
    CONFIG_ITEM_ID,
    SEQ,
    MODULE_CODE,
    DIRECTORY_CODE,
    CONFIG_ITEM_CODE,
    CONFIG_ITEM_NAME,
    IS_VISIABLE,
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
    $pojo.seq,
    '$!pojo.moduleCode',
    '$!pojo.directoryCode',
    '$pojo.configItemCode',
    '$pojo.configItemName',
    '$pojo.isVisiable',
    CURRENT_TIMESTAMP(),
    '$!pojo.remark',
    $!pojo.operatorId,
    $!pojo.channelId
)
#end