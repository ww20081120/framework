SELECT ATTR_ID,
       ATTR_VALUE_ID,
       VALUE_MARK,
       VALUE,
       LINK_ATTR_ID
FROM   ATTR_VALUE
#if($attrId)
WHERE  ATTR_ID = :attrId
#end
