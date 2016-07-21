SELECT D.DIRECTORY_CODE,
       D.DIRECTORY_NAME,
       D.PARENT_DIRECTORY_CODE,
       D.REMARK
  FROM DIRECTORY D
 WHERE 1 = 1
  #if($directory.directoryCode)
    AND D.DIRECTORY_CODE = :directory.directoryCode
  #end
  #if($directory.directoryName)
    AND D.DIRECTORY_NAME = :directory.directoryName
  #end
