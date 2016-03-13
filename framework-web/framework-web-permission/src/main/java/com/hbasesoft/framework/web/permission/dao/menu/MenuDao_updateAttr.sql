UPDATE MENU M
   SET M.MENU_NAME = :pojo.menuName,
   #if($pojo.url)
			 M.URL = :pojo.url,
	 #end
	 #if($pojo.functionCode)
			 M.FUNCTION_CODE = :pojo.functionCODE,
	 #end
			 M.ICON_URL = :pojo.iconUrl
 WHERE M.RESOURCE_ID = :pojo.resourceId