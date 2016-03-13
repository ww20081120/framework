SELECT * FROM ANNOUNCEMENT T
#if($pojo)
WHERE 1 = 1
 	#if($pojo.announcementId)
        AND T.ANNOUNCEMENT_ID = :pojo.announcementId
    #end
#end
 