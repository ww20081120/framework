UPDATE T_MANAGER_ORG
   SET ORG_NAME = :pojo.orgName,
       ORG_CODE = :pojo.orgCode,
       OWNER_AREA = :pojo.ownerArea
 WHERE ORG_ID = :pojo.orgId