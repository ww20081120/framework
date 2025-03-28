DROP TABLE IF EXISTS `t_student_course`;

DROP TABLE IF EXISTS `t_student`;

DROP TABLE IF EXISTS `t_course`;

CREATE TABLE `t_student` (
	id       varchar(32)       primary key,
	name     varchar(32)       not null,
	age      int(3)            not null
);

CREATE TABLE `t_course` (
    id       varchar(32)       primary key,
    course_name varchar(64)    not null,
    remark   varchar(255)
);

CREATE TABLE `t_student_course` (
    id       varchar(32)       primary key,
    student_id varchar(32)     not null,
    course_id  varchar(32)     not null,
    score      int(3)
);

insert into t_student(id, name, age) values ('1', '张三', 18);
insert into t_student(id, name, age) values ('2', '李四', 19);
insert into t_student(id, name, age) values ('3', '王五', 18);
insert into t_student(id, name, age) values ('4', '赵六', 17);

insert into t_course(id, course_name, remark) values ('1', '语文', '语文是赵老师的课');
insert into t_course(id, course_name, remark) values ('2', '数学', '数学是李老师的课');
insert into t_course(id, course_name, remark) values ('3', '英语', '英语是王老师的课');

insert into t_student_course (id, student_id, course_id, score) values ('1', '1', '1', 69);
insert into t_student_course (id, student_id, course_id, score) values ('2', '1', '2', 73);
insert into t_student_course (id, student_id, course_id, score) values ('3', '1', '3', 56);

insert into t_student_course (id, student_id, course_id, score) values ('4', '2', '1', 19);
insert into t_student_course (id, student_id, course_id, score) values ('5', '2', '2', 32);
insert into t_student_course (id, student_id, course_id, score) values ('6', '2', '3', 43);

insert into t_student_course (id, student_id, course_id, score) values ('7', '3', '1', 89);
insert into t_student_course (id, student_id, course_id, score) values ('8', '3', '2', 94);
insert into t_student_course (id, student_id, course_id, score) values ('9', '3', '3', 99)