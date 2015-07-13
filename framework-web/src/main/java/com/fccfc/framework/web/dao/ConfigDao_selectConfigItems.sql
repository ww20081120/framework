select CONFIG_ITEM_ID as id,
       CONFIG_ITEM_NAME,
       DIRECTORY_CODE,
       MODULE_CODE,
       CONFIG_ITEM_CODE,
       IS_VISIABLE,
       UPDATE_TIME,
       REMARK
  from config_item
 where DIRECTORY_CODE = :directory