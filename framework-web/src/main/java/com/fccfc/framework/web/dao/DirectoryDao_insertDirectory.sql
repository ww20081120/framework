INSERT INTO DIRECTORY
  (DIRECTORY_CODE, DIRECTORY_NAME, PARENT_DIRECTORY_CODE, REMARK)
VALUES
  (:directory.directoryCode,
   :directory.directoryName,
   :directory.parentDirectoryCode,
   :directory.remark)