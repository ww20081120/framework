UPDATE MENU M
   SET M.MENU_NAME = :pojo.menuName,
   #if($pojo.url)
			 M.URL = :pojo.url,
	 #end
	 #if($pojo.functionId)
			 M.FUNCTION_ID = :pojo.functionId,
	 #end
			 M.ICON_URL = :pojo.iconUrl
 WHERE M.RESOURCE_ID = :pojo.resourceId