CREATE TABLE t_tx_check_info (
  id text,
  mark text,
  result blob,
  createTime timestamp,
  PRIMARY KEY(id, mark));
  
CREATE TABLE t_tx_clientinfo (
  id text PRIMARY KEY, 
  mark text,
  context text,
  args blob,
  maxRetryTimes int,
  currentRetryTimes int,
  retryConfigs text,
  nextRetryTime timestamp,
  clientInfo text,
  createTime timestamp);
  
CREATE INDEX i_tx_clientinfo_nextRetryTime ON t_tx_clientinfo (nextRetryTime);