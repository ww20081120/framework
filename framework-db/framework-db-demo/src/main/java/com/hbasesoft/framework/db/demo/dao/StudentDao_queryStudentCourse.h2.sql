select 
   s.id, s.name, s.age, sc.score, c.course_name
from 
   t_student s,
   t_course c,
   t_student_course sc
where
   s.id = sc.student_id
and
   c.id = sc.course_id
#if($entity.name)
  and s.name like :entity.name
#end

#if($entity.age)
  and s.age = :entity.age
#end