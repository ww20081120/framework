UPDATE ADMIN
   SET ADMIN_NAME = :adminPojo.adminName,
       HEAD_IMG = :adminPojo.headImg,
       GENER = :adminPojo.gener,
       EMAIL = :adminPojo.email,
       PHONE = :adminPojo.phone,
       ADDRESS = :adminPojo.address
 WHERE ADMIN_ID = :adminPojo.adminId