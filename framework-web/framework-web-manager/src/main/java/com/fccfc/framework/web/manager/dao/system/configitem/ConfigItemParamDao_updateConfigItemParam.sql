UPDATE CONFIG_ITEM_PARAM
   SET PARAM_CODE = :pojo.paramCode,
       PARAM_NAME = :pojo.paramName,
       PARAM_VALUE = :pojo.paramValue,
       DEFAULT_PARAM_VALUE = :pojo.defaultParamValue,
       DATA_TYPE = :pojo.dataType,
       INPUT_TYPE = :pojo.inputType,
       VALUE_SCRIPT = :pojo.valueScript,
       UPDATE_TIME = :pojo.updateTime,
       REMARK = :pojo.remark
 WHERE CONFIG_ITEM_ID = :pojo.configItemId
   AND PARAM_CODE = :oldParamCode