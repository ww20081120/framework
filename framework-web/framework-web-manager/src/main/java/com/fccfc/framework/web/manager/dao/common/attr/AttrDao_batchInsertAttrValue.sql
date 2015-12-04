INSERT INTO ATTR_VALUE
(
    ATTR_ID,
    ATTR_VALUE_ID,
    VALUE_MARK,
    VALUE,
    LINK_ATTR_ID
)
VALUES
#foreach($pojo in $pojoList)
    #if($velocityCount != 1),#end
    (
        $pojo.attrId,
        $pojo.attrValueId,
        '$pojo.valueMark',
        '$pojo.value',
        $pojo.linkAttrId
    )
#end