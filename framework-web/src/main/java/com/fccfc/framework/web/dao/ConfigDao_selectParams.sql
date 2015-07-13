select A.PARAM_CODE,
       A.PARAM_NAME,
       A.PARAM_VALUE,
       A.DEFAULT_PARAM_VALUE,
       A.REMARK,
       A.UPDATE_TIME,
       B.DATA_TYPE_NAME,
       C.INPUT_TYPE_NAME
  from config_item_param A, data_type B, input_type C
 WHERE A.CONFIG_ITEM_ID = :itemId
   AND A.INPUT_TYPE = C.INPUT_TYPE
   AND A.DATA_TYPE = B.DATA_TYPE