SELECT CONFIG_ITEM_ID,
       PARAM_CODE,
       PARAM_NAME,
       PARAM_VALUE,
       DEFAULT_PARAM_VALUE,
       DATA_TYPE,
       INPUT_TYPE,
       VALUE_SCRIPT,
       UPDATE_TIME,
       REMARK
  FROM CONFIG_ITEM_PARAM
 WHERE 1=1
#if($configItemId)
AND CONFIG_ITEM_ID = :configItemId
#end
#if($paramCode)
AND PARAM_CODE = :paramCode
#end
