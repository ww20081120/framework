INSERT INTO T_MSG_MESSAGE_HISTORY
VALUES
  (:history.messageId,
   :history.receivers,
   :history.sender,
   :history.messageType,
   :history.messageTemplateId,
   :history.subject,
   :history.content,
   :history.attachmentsNum,
   :history.createTime,
   :history.sendTime,
   :history.sendTimes,
   :history.result,
   :history.expDate,
   :history.extendAttrs)