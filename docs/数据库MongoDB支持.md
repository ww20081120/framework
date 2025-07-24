### MongoDB支持文档

本框架支持使用统一的BaseDao API操作MongoDB数据库，开发者可以使用与操作关系型数据库相同的API来操作MongoDB，实现一份API、多种存储的架构。

#### 特性

1. **统一API** - 使用与SQL数据库相同的BaseDao接口
2. **透明切换** - 可以轻松在关系型数据库和MongoDB之间切换
3. **原生MongoDB支持** - 支持MongoDB特有的聚合查询和操作
4. **配置简单** - 通过简单的配置即可启用MongoDB支持

#### 配置MongoDB数据源

在application.yml中配置MongoDB数据源：

```yaml
project: #项目信息
 name: db-demo
 
spring: #应用配置
  application:
    name: ${project.name}
  
master: #主数据库配置
 db:
  type: mongodb # 指定数据库类型为mongodb
 mongodb:
  url: mongodb://localhost:27017/testdb # MongoDB连接URL
```

#### 创建MongoDB DAO

创建MongoDB DAO与创建SQL DAO类似，但需要使用@Dao4Mongo注解：

```java
@Dao4Mongo
public interface IStudentMongoDBDao extends BaseDao<StudentEntity> {
    // 可以添加自定义方法
    
    /**
     * 创建集合（在MongoDB中相当于创建表）
     */
    @Sql("create collection if not exists t_student")
    void createCollection();
    
    /**
     * 使用MongoDB聚合查询
     */
    @Sql("{aggregate: 't_student', pipeline: [{$match: {age: {$gt: 18}}}]}")
    List<StudentEntity> findAdultStudents();
}
```

#### 实体类定义

MongoDB实体类与SQL实体类基本相同，但可以利用MongoDB的文档特性：

```java
@Entity("t_student") // MongoDB中的集合名
public class StudentEntity extends BaseEntity {
    /** serialVersionUID */
    private static final long serialVersionUID = 1L;
    
    /** ID */
    @Id
    private String id;
    
    /** 姓名 */
    private String name;
    
    /** 年龄 */
    private Integer age;
    
    /** 成绩列表 */
    private List<Score> scores; // 嵌套文档
    
    // getters and setters
}
```

#### 使用BaseDao API操作MongoDB

由于使用了统一的BaseDao API，大部分操作与SQL数据库完全相同：

```java
@Service
public class StudentService {
    @Resource
    private IStudentMongoDBDao studentDao;
    
    /**
     * 保存学生信息
     */
    @Transactional
    public void saveStudent(StudentEntity student) {
        studentDao.save(student);
    }
    
    /**
     * 批量保存学生信息
     */
    @Transactional
    public void saveStudents(List<StudentEntity> students) {
        studentDao.saveBatch(students);
    }
    
    /**
     * 根据ID获取学生信息
     */
    public StudentEntity getStudentById(String id) {
        return studentDao.get(id);
    }
    
    /**
     * 使用QueryWrapper查询学生
     */
    public List<StudentEntity> getStudentsByName(String name) {
        return studentDao.query(q -> q.eq("name", name));
    }
    
    /**
     * 使用LambdaQueryWrapper查询学生
     */
    public List<StudentEntity> getStudentsByAgeRange(int minAge, int maxAge) {
        return studentDao.queryByLambda(
            q -> q.ge(StudentEntity::getAge, minAge).le(StudentEntity::getAge, maxAge)
        );
    }
    
    /**
     * 分页查询学生
     */
    public PagerList<StudentEntity> getStudentsByPage(int pageIndex, int pageSize) {
        return studentDao.queryPager(q -> q.orderByAsc("name"), pageIndex, pageSize);
    }
    
    /**
     * 更新学生信息
     */
    @Transactional
    public void updateStudentAge(String id, int newAge) {
        studentDao.update(q -> q.set("age", newAge).eq("id", id));
    }
    
    /**
     * 删除学生
     */
    @Transactional
    public void deleteStudent(String id) {
        studentDao.deleteById(id);
    }
}
```

#### MongoDB特有功能使用

对于MongoDB特有的功能，如聚合查询，可以使用@Sql注解：

```java
@Dao4Mongo
public interface IStudentMongoDBDao extends BaseDao<StudentEntity> {
    /**
     * 使用聚合管道查询
     */
    @Sql("{aggregate: 't_student', pipeline: [{$match: {age: {$gt: $minAge}}}, {$sort: {age: 1}}]}")
    List<StudentEntity> findStudentsByAge(int minAge);
    
    /**
     * 使用聚合管道进行分组统计
     */
    @Sql("{aggregate: 't_student', pipeline: [{$group: {_id: '$age', count: {$sum: 1}}}]}")
    List<AgeStatistics> getAgeStatistics();
    
    /**
     * 使用聚合管道进行复杂查询
     */
    @Sql("{aggregate: 't_student', pipeline: [{$lookup: {from: 't_course', localField: 'courseId', foreignField: '_id', as: 'course'}}, {$match: {'course.name': $courseName}}]}")
    List<StudentWithCourse> findStudentsByCourseName(String courseName);
}
```

在Service中使用：

```java
@Service
public class StudentMongoService {
    @Resource
    private IStudentMongoDBDao studentDao;
    
    /**
     * 使用聚合查询查找特定年龄段的学生
     */
    public List<StudentEntity> findStudentsByAge(int minAge) {
        // 注意：参数传递方式与SQL略有不同
        Map<String, Object> params = new HashMap<>();
        params.put("minAge", minAge);
        DataParam dataParam = new DataParam();
        dataParam.setParamMap(params);
        dataParam.setBeanType(StudentEntity.class);
        
        // 这里需要使用query方法执行原生MongoDB查询
        return studentDao.query("{aggregate: 't_student', pipeline: [{$match: {age: {$gt: $minAge}}}, {$sort: {age: 1}}]}", dataParam);
    }
    
    /**
     * 获取年龄统计信息
     */
    public List<AgeStatistics> getAgeStatistics() {
        Map<String, Object> params = new HashMap<>();
        DataParam dataParam = new DataParam();
        dataParam.setParamMap(params);
        dataParam.setBeanType(AgeStatistics.class);
        dataParam.setReturnType(List.class);
        
        return (List<AgeStatistics>) studentDao.query(
            "{aggregate: 't_student', pipeline: [{$group: {_id: '$age', count: {$sum: 1}}}]}", 
            dataParam
        );
    }
}
```

#### 在同一应用中使用多种数据库

框架支持在同一个应用中使用多种数据库，通过不同的DAO实现来切换：

```java
@Service
public class StudentService {
    // SQL数据库DAO
    @Resource(name = "studentMySqlDao")
    private IStudentDao studentMySqlDao;
    
    // MongoDB DAO
    @Resource(name = "studentMongoDBDao")
    private IStudentMongoDBDao studentMongoDBDao;
    
    /**
     * 根据配置选择数据库类型
     */
    private BaseDao<StudentEntity> getDao(String dbType) {
        if ("mongodb".equals(dbType)) {
            return studentMongoDBDao;
        } else {
            return studentMySqlDao;
        }
    }
    
    /**
     * 保存学生信息到指定数据库
     */
    @Transactional
    public void saveStudent(StudentEntity student, String dbType) {
        getDao(dbType).save(student);
    }
    
    /**
     * 查询学生信息从指定数据库
     */
    public List<StudentEntity> getStudents(String dbType) {
        return getDao(dbType).queryAll();
    }
}
```

配置多个数据源：

```yaml
# MySQL数据源
master: 
 db:
  type: mysql
  url: jdbc:mysql://localhost:3306/testdb
  username: root
  password: root

# MongoDB数据源
mongodb:
 db:
  type: mongodb
  url: mongodb://localhost:27017/testdb
```

#### 注意事项

1. **实体类设计** - MongoDB支持嵌套文档，可以更好地利用这一特性设计实体类
2. **索引创建** - 在MongoDB中合理创建索引以提高查询性能
3. **聚合查询** - 对于复杂查询，MongoDB的聚合管道功能非常强大
4. **事务支持** - MongoDB 4.0+支持多文档事务，但需要副本集或分片集群
5. **参数绑定** - MongoDB查询中的参数绑定语法与SQL略有不同

通过以上方式，开发者可以使用统一的API操作MongoDB，同时保留MongoDB特有的强大功能，实现SQL和NoSQL数据库的无缝切换。