update 
    employee t
 set 
 	t.name = :#{#entity.name}, 
 	t.age = :#{#entity.age} 
where 
    t.id = :#{#entity.id}