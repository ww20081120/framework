INSERT INTO CONFIG_ITEM
(
    CONFIG_ITEM_ID,
    DIRECTORY_CODE,
    MODULE_CODE,
    CONFIG_ITEM_CODE,
    CONFIG_ITEM_NAME,
    IS_VISIABLE,
    UPDATE_TIME,
    REMARK
)
VALUES
#foreach($pojo in $pojoList)
    #if($velocityCount != 1) , #end
    (
        $pojo.configItemId,
        #if($pojo.directoryCode)'$pojo.directoryCode' #else NULL #end ,
        #if($!pojo.moduleCode)'$!pojo.moduleCode' #else NULL #end ,
        '$pojo.configItemCode',
        '$pojo.configItemName',
        '$pojo.isVisiable',
        CURRENT_TIMESTAMP(),
        '$!pojo.remark'
    )
#end
    