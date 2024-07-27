DROP TABLE IF EXISTS `t_staff`;

CREATE TABLE `t_staff` (
	id INT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1), -- 自增主键
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    position VARCHAR(100),
    department VARCHAR(100),
    salary DECIMAL(10, 2),
    hire_date DATE,
    PRIMARY KEY (id)
)
