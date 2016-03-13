INSERT INTO ANNOUNCEMENT
  (TITLE, CONTENT, CREATE_TIME, STATE, STATE_DATE, OPERATOR_ID, COMMENTS)
VALUES
  (:pojo.title,
   :pojo.content,
   sysdate(),
   :pojo.state,
   sysdate(),
   :pojo.operatorId,
   :pojo.comments)