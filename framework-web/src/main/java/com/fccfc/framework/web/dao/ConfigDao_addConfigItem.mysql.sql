insert into config_item
  (MODULE_CODE,
   DIRECTORY_CODE,
   CONFIG_ITEM_CODE,
   CONFIG_ITEM_NAME,
   IS_VISIABLE,
   UPDATE_TIME,
   REMARK)
VALUES
  (:module,
   :directory,
   :code,
   :name,
   :vasiable,
   current_timestamp(),
   :remark)