UPDATE DICTIONARY
   SET DICT_CODE = :pojo.dictCode,
       DICT_NAME = :pojo.dictName,
       REMARK = :pojo.remark
 WHERE DICT_CODE = :oldDictCode