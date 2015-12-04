INSERT INTO AREA_RANGE
  (AREA_ID, SEQ, LONGITUDE, LATITUDE)
VALUES
  (LAST_INSERT_ID(),
   :pojo.seq,
   :pojo.longitude,
   :pojo.latitude)