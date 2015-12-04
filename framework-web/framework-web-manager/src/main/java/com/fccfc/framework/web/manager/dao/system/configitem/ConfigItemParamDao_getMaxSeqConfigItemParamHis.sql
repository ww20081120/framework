SELECT IFNULL
(
    (SELECT MAX(SEQ) + 1
       FROM CONFIG_ITEM_PARAM_HISTORY
      WHERE CONFIG_ITEM_ID = :configItemId
        AND PARAM_CODE = :paramCode), 1
)