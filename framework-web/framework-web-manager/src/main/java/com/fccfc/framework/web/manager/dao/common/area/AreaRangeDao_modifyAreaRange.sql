UPDATE AREA_RANGE
SET    LONGITUDE = :pojo.longitude,
       LATITUDE = :pojo.latitude
WHERE  AREA_ID = :pojo.areaId
