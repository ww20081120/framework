INSERT INTO CONFIG_ITEM_PARAM
(
    CONFIG_ITEM_ID,
    PARAM_CODE,
    PARAM_NAME,
    PARAM_VALUE,
    DEFAULT_PARAM_VALUE,
    DATA_TYPE,
    INPUT_TYPE,
    VALUE_SCRIPT,
    UPDATE_TIME,
    REMARK
)
VALUES
(
    :pojo.configItemId,
    :pojo.paramCode,
    :pojo.paramName,
    :pojo.paramValue,
    :pojo.defaultParamValue,
    :pojo.dataType,
    :pojo.inputType,
    :pojo.valueScript,
    :pojo.updateTime,
    :pojo.remark
)

