UPDATE MESSAGE_TEMPLATE
SET    MESSAGE_TEMPLATE_CODE = :pojo.messageTemplateCode,
       NAME = :pojo.name,
       TEMPLATE = :pojo.template,
       STATE = :pojo.state,
       CONTACT_CHANNEL_IDS = :pojo.contactChannelIds,
       STATE_TIME = :pojo.stateTime,
       DELAY = :pojo.delay,
       RESEND_TIMES = :pojo.resendTimes,
       SAVE_HISTORY = :pojo.saveHistory,
       SAVE_DAY = :pojo.saveDay,
       CREATE_TIME = :pojo.createTime
WHERE  MESSAGE_TEMPLATE_ID = :pojo.messageTemplateId