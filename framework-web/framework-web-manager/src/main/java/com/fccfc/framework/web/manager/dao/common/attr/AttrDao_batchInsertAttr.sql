INSERT INTO ATTR
(
    ATTR_ID,
    ATTR_NAME,
    ATTR_TYPE,
    PARENT_ATTR_ID,
    ATTR_CODE,
    VISIBLE,
    INSTANTIATABLE,
    DEFAULT_VALUE,
    INPUT_TYPE,
    DATA_TYPE,
    VALUE_SCRIPT
)
VALUES
#foreach ($pojo in $pojoList)
    #if($velocityCount != 1),#end
    (
     $pojo.attrId,
     '$pojo.attrName',
     '$pojo.attrType',
     $pojo.parentAttrId,
     '$pojo.attrCode',
     '$pojo.visible',
     '$pojo.instantiatable',
     '$pojo.defaultValue',
     '$pojo.dataType',
     '$pojo.inputType',
     '$pojo.valueScript'
    )
#end