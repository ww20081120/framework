UPDATE 
	MENU M SET M.PARENT_RESOURCE_ID = :menu.parentResourceId, 
	M.SEQ = :menu.seq, 
	M.IS_LEAF = :menu.isLeaf 
WHERE 
	M.RESOURCE_ID = :menu.resourceId