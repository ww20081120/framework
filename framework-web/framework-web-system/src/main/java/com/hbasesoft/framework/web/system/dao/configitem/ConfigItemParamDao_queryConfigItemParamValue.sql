SELECT CONFIG_ITEM_ID,
       PARAM_CODE,
       PARAM_VALUE_ID,
       VALUE_MARK,
       VALUE,
       REMARK
  FROM CONFIG_ITEM_PARAM_VALUE
  WHERE 1=1
#if($configItemId)
AND CONFIG_ITEM_ID = :configItemId
#end
#if($paramCode)
AND PARAM_CODE = :paramCode
#end