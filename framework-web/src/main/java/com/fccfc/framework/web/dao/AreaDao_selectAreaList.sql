SELECT * FROM AREA A
#if($area)
WHERE 1 = 1
	#if($area.areaId)
	AND A.AREA_ID = :area.areaId
	#end
	#if($area.areaCode)
	AND A.AREA_CODE = :area.areaCode
	#end
	#if($area.areaName)
	AND A.AREA_NAME LIKE :area.areaName
	#end
	#if($area.areaType)
	AND A.AREA_TYPE = :area.areaType
	#end
	#if($area.parentAreaId)
	AND A.PARENT_AREA_ID = :area.parentAreaId
	#end
#end