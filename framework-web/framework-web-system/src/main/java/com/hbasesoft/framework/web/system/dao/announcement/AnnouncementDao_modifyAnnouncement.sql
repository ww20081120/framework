UPDATE ANNOUNCEMENT
   SET TITLE       = :pojo.title,
       CONTENT     = :pojo.content,
       STATE       = :pojo.state,
       OPERATOR_ID = :pojo.operatorId,
       COMMENTS    = :pojo.comments
 WHERE ANNOUNCEMENT_ID = :pojo.announcementId