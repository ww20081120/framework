SELECT DICT_DATA_ID,
       DICT_CODE,
       DICT_DATA_NAME,
       DICT_DATA_VALUE,
       IS_FIXED,
       IS_CANCEL
  FROM DICTIONARY_DATA
  WHERE 1=1
  #if($dictCode)
  AND DICT_CODE = :dictCode
  #end