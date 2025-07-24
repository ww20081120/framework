### 高级JPA用法指南

本框架除了提供基于QueryWrapper的现代化API外，还完整支持JPA的高级特性，包括Criteria API和原生SQL/HQL查询。对于需要复杂查询或特定数据库功能的场景，JPA提供了更强大的能力。

#### JPA核心组件

1. **EntityManager** - JPA的核心接口，用于实体的持久化操作
2. **Criteria API** - 类型安全的动态查询构建器
3. **JPQL** - Java Persistence Query Language，面向对象的查询语言
4. **Native SQL** - 原生SQL查询支持

#### BaseJpaDao接口详解

BaseJpaDao扩展了BaseDao，提供了更多JPA特有的方法：

```java
public interface BaseJpaDao<T extends BaseEntity> extends BaseDao<T> {
    // Criteria API相关方法
    CriteriaBuilder criteriaBuilder(); // 获取CriteriaBuilder实例
    <M> M getByCriteria(CriteriaQuery<M> criteria); // 根据CriteriaQuery查询单个结果
    <M> List<M> queryByCriteria(CriteriaQuery<M> criteria); // 根据CriteriaQuery查询结果列表
    <M> PagerList<M> queryPagerByCriteria(CriteriaQuery<M> criteria, int pageIndex, int pageSize); // 分页查询
    
    // CriteriaUpdate/CriteriaDelete
    void updateByCriteria(CriteriaUpdate<T> criteria); // 根据CriteriaUpdate更新
    void deleteByCriteria(CriteriaDelete<T> criteria); // 根据CriteriaDelete删除
    
    // Specification模式（简化Criteria API使用）
    T getBySpecification(CriterialQuerySpecification<T> specification); // 根据Specification查询单个结果
    <M> M getBySpecification(CriterialQuerySpecification<T> specification, Class<M> clazz); // 查询并转换类型
    List<T> queryBySpecification(CriterialQuerySpecification<T> specification); // 根据Specification查询结果列表
    <M> List<M> queryBySpecification(CriterialQuerySpecification<T> specification, Class<M> clazz); // 查询并转换类型
    PagerList<T> queryPagerBySpecification(CriterialQuerySpecification<T> specification, int pageIndex, int pageSize); // 分页查询
    <M> PagerList<M> queryPagerBySpecification(CriterialQuerySpecification<T> specification, int pageIndex, int pageSize, Class<M> clazz); // 分页查询并转换类型
    void updateBySpecification(CriterialUpdateSpecification<T> specification); // 根据Specification更新
    void deleteBySpecification(CriterialDeleteSpecification<T> specification); // 根据Specification删除
    
    // HQL/JPQL查询
    T getByHql(String hql); // 根据HQL查询单个结果
    List<T> queryByHql(String hql); // 根据HQL查询结果列表
    List<T> queryByHqlParam(String hql, Object... param); // 带参数的HQL查询
    
    // 原生SQL查询
    List<T> queryBySql(String sql); // 根据原生SQL查询结果列表
    <M> List<M> queryBySql(String sql, Class<M> clazz); // 原生SQL查询并转换类型
    int updateBySql(String sql) throws DaoException; // 执行原生SQL更新语句
    
    // 存储过程调用
    List<T> executeProcedure(String procedureSql, Object... params); // 执行存储过程
    
    // 批处理
    void executeBatch(String sql, Collection<Object[]> objcts, int commitNumber); // 批量执行SQL
    
    // EntityManager操作
    void flush(); // 强制同步到数据库
    void clear(); // 清除持久化上下文
}
```

#### Criteria API使用示例

Criteria API是JPA提供的类型安全的查询构建器，可以避免字符串拼接带来的错误。

```java
@Service
public class StudentJpaService {
    @Resource
    private IStudentDao studentDao;
    
    /**
     * 使用Criteria API进行复杂查询
     */
    public List<StudentEntity> findStudentsByCriteria() {
        CriteriaBuilder cb = studentDao.criteriaBuilder();
        CriteriaQuery<StudentEntity> query = cb.createQuery(StudentEntity.class);
        Root<StudentEntity> root = query.from(StudentEntity.class);
        
        // 构建查询条件：年龄大于18且姓名包含"张"
        Predicate agePredicate = cb.greaterThan(root.get("age"), 18);
        Predicate namePredicate = cb.like(root.get("name"), "%张%");
        query.where(cb.and(agePredicate, namePredicate));
        
        // 添加排序
        query.orderBy(cb.asc(root.get("age")));
        
        return studentDao.queryByCriteria(query);
    }
    
    /**
     * 使用Criteria API进行聚合查询
     */
    public Long countStudents() {
        CriteriaBuilder cb = studentDao.criteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<StudentEntity> root = query.from(StudentEntity.class);
        
        // COUNT查询
        query.select(cb.count(root));
        
        return studentDao.getByCriteria(query);
    }
    
    /**
     * 使用Criteria API进行分组查询
     */
    public List<Object[]> getStudentStatistics() {
        CriteriaBuilder cb = studentDao.criteriaBuilder();
        CriteriaQuery<Object[]> query = cb.createQuery(Object[].class);
        Root<StudentEntity> root = query.from(StudentEntity.class);
        
        // 按年龄分组，统计每个年龄段的人数
        query.multiselect(root.get("age"), cb.count(root))
             .groupBy(root.get("age"));
        
        return studentDao.queryByCriteria(query);
    }
    
    /**
     * 使用Criteria API进行更新操作
     */
    @Transactional
    public void updateStudentAgeByCriteria() {
        CriteriaBuilder cb = studentDao.criteriaBuilder();
        CriteriaUpdate<StudentEntity> update = cb.createCriteriaUpdate(StudentEntity.class);
        Root<StudentEntity> root = update.from(StudentEntity.class);
        
        // 将年龄小于18的学生年龄设置为18
        update.set("age", 18)
              .where(cb.lessThan(root.get("age"), 18));
        
        studentDao.updateByCriteria(update);
    }
    
    /**
     * 使用Criteria API进行删除操作
     */
    @Transactional
    public void deleteStudentsByCriteria() {
        CriteriaBuilder cb = studentDao.criteriaBuilder();
        CriteriaDelete<StudentEntity> delete = cb.createCriteriaDelete(StudentEntity.class);
        Root<StudentEntity> root = delete.from(StudentEntity.class);
        
        // 删除年龄小于18的学生
        delete.where(cb.lessThan(root.get("age"), 18));
        
        studentDao.deleteByCriteria(delete);
    }
}
```

#### Specification模式使用示例

Specification模式是Criteria API的简化封装，使用更加便捷：

```java
@Service
public class StudentJpaService {
    @Resource
    private IStudentDao studentDao;
    
    /**
     * 使用Specification查询学生
     */
    public List<StudentEntity> findStudentsBySpecification() {
        return studentDao.queryBySpecification((root, query, cb) -> {
            // 构建条件：年龄大于18且姓名包含"张"
            Predicate agePredicate = cb.greaterThan(root.get("age"), 18);
            Predicate namePredicate = cb.like(root.get("name"), "%张%");
            return cb.and(agePredicate, namePredicate);
        });
    }
    
    /**
     * 使用Specification查询并转换结果类型
     */
    public List<StudentStatistics> getStudentStatisticsBySpecification() {
        return studentDao.queryBySpecification(
            (root, query, cb) -> {
                // 按年龄分组统计
                query.multiselect(
                    root.get("age").alias("age"),
                    cb.count(root).alias("count")
                ).groupBy(root.get("age"));
                return null; // 没有过滤条件
            },
            StudentStatistics.class // 转换为目标类型
        );
    }
    
    /**
     * 使用Specification进行更新
     */
    @Transactional
    public void updateStudentAgeBySpecification() {
        studentDao.updateBySpecification((root, update, cb) -> {
            // 将年龄小于18的学生年龄设置为18
            update.set("age", 18);
            return cb.lessThan(root.get("age"), 18);
        });
    }
    
    /**
     * 使用Specification进行删除
     */
    @Transactional
    public void deleteStudentsBySpecification() {
        studentDao.deleteBySpecification((root, delete, cb) -> {
            // 删除年龄小于18的学生
            return cb.lessThan(root.get("age"), 18);
        });
    }
}
```

#### HQL/JPQL使用示例

HQL是Hibernate查询语言，JPQL是JPA查询语言，它们都是面向对象的查询语言：

```java
@Service
public class StudentJpaService {
    @Resource
    private IStudentDao studentDao;
    
    /**
     * 使用HQL查询学生
     */
    public List<StudentEntity> findStudentsByHql() {
        String hql = "FROM StudentEntity s WHERE s.age > :age AND s.name LIKE :name ORDER BY s.age";
        return studentDao.queryByHqlParam(hql, 18, "%张%");
    }
    
    /**
     * 使用HQL进行聚合查询
     */
    public Long countStudentsByHql() {
        String hql = "SELECT COUNT(s) FROM StudentEntity s";
        return studentDao.getByHql(hql);
    }
    
    /**
     * 使用HQL进行分组查询
     */
    public List<Object[]> getStudentStatisticsByHql() {
        String hql = "SELECT s.age, COUNT(s) FROM StudentEntity s GROUP BY s.age";
        return studentDao.queryByHql(hql);
    }
}
```

#### 原生SQL使用示例

对于复杂的查询或数据库特定功能，可以使用原生SQL：

```java
@Service
public class StudentJpaService {
    @Resource
    private IStudentDao studentDao;
    
    /**
     * 使用原生SQL查询学生
     */
    public List<StudentEntity> findStudentsBySql() {
        String sql = "SELECT * FROM t_student WHERE age > ? AND name LIKE ? ORDER BY age";
        return studentDao.queryBySql(sql, 18, "%张%");
    }
    
    /**
     * 使用原生SQL查询并转换结果类型
     */
    public List<StudentStatistics> getStudentStatisticsBySql() {
        String sql = "SELECT age, COUNT(*) as count FROM t_student GROUP BY age";
        return studentDao.queryBySql(sql, StudentStatistics.class);
    }
    
    /**
     * 使用原生SQL进行更新
     */
    @Transactional
    public int updateStudentAgeBySql() {
        String sql = "UPDATE t_student SET age = 18 WHERE age < 18";
        try {
            return studentDao.updateBySql(sql);
        } catch (DaoException e) {
            // 处理异常
            throw new RuntimeException("更新学生年龄失败", e);
        }
    }
}
```

#### 存储过程调用示例

```java
@Service
public class StudentJpaService {
    @Resource
    private IStudentDao studentDao;
    
    /**
     * 调用存储过程
     */
    public List<StudentEntity> callStudentProcedure() {
        String procedureSql = "{call get_students_by_age(?)}";
        return studentDao.executeProcedure(procedureSql, 18);
    }
}
```

#### 批处理操作示例

```java
@Service
public class StudentJpaService {
    @Resource
    private IStudentDao studentDao;
    
    /**
     * 批量插入学生数据
     */
    @Transactional
    public void batchInsertStudents(List<StudentEntity> students) {
        // 准备数据
        List<Object[]> batchData = students.stream()
            .map(s -> new Object[]{s.getId(), s.getName(), s.getAge()})
            .collect(Collectors.toList());
        
        // 批量执行
        studentDao.executeBatch(
            "INSERT INTO t_student (id, name, age) VALUES (?, ?, ?)",
            batchData,
            1000 // 每1000条提交一次
        );
    }
}
```

#### 性能优化建议

1. **合理使用懒加载** - 避免N+1查询问题
2. **使用分页查询** - 避免一次性加载大量数据
3. **适当使用缓存** - 减少数据库访问次数
4. **批量操作** - 提高数据操作效率
5. **连接池配置** - 合理配置数据库连接池参数

通过合理使用JPA的高级特性，可以构建更复杂、更高效的数据库访问逻辑，满足企业级应用的需求。