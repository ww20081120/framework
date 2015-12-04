SELECT A.DICT_DATA_ID,
       A.DICT_CODE,
       A.DICT_DATA_NAME,
       A.DICT_DATA_VALUE,
       A.IS_FIXED,
       A.IS_CANCEL
  FROM DICTIONARY_DATA A
  WHERE 1 = 1
  #if($dictCode)
  AND A.DICT_CODE = :dictCode
  #end