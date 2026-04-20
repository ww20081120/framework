### QueryWrapper使用指南

QueryWrapper是框架提供的一个强大的条件构造器，可以帮助开发者以链式调用的方式构造查询条件，无需手写SQL。

#### 基本用法

框架提供两种条件构造器，满足不同场景的需求：

1. **QueryWrapper** - 基于字段名的条件构造器
   - 直接使用字符串字段名
   - 适合动态字段场景
   - 无编译时类型检查

2. **LambdaQueryWrapper** - 基于Lambda表达式的类型安全条件构造器
   - 使用Lambda表达式引用字段
   - **编译时类型检查**：字段名拼写错误会在编译时发现
   - **重构友好**：IDE重命名字段时自动更新查询代码
   - **代码可读性高**：方法引用清晰表达查询意图
   - **避免硬编码**：不再需要魔法字符串

#### LambdaQueryWrapper工作原理

> 代码位置：`framework-db/framework-db-core/src/main/java/com/hbasesoft/framework/db/core/criteria/LambdaQueryWrapper.java`

**核心机制**：

LambdaQueryWrapper通过Java Lambda表达式的序列化特性，在运行时提取字段名，实现类型安全的查询构造。

```java
// SFunction接口定义
@FunctionalInterface
public interface SFunction<T, R> extends Function<T, R>, Serializable {
}
```

**工作流程**：

1. **Lambda表达式创建**：使用方法引用 `StudentEntity::getName`
2. **序列化解析**：通过 `SerializedLambda.resolve()` 提取Lambda元信息
3. **字段名提取**：从Lambda的方法名（如 `getName`）解析出字段名（如 `name`）
4. **缓存优化**：解析结果缓存，避免重复解析开销

**类型安全优势对比**：

| 特性 | QueryWrapper | LambdaQueryWrapper |
|------|-------------|-------------------|
| 字段名类型 | 字符串（硬编码） | Lambda表达式（类型引用） |
| 编译检查 | ❌ 运行时发现错误 | ✅ 编译时发现错误 |
| 重构支持 | ❌ 需手动修改 | ✅ IDE自动更新 |
| 代码提示 | ❌ 无自动补全 | ✅ IDE智能提示 |
| 可读性 | 一般 | 高 |

#### 常用方法

##### 比较操作
```java
// 等于
queryWrapper.eq("name", "张三")
// 等价于 LambdaQueryWrapper
lambdaQueryWrapper.eq(StudentEntity::getName, "张三")

// 不等于
queryWrapper.ne("name", "张三")
lambdaQueryWrapper.ne(StudentEntity::getName, "张三")

// 大于
queryWrapper.gt("age", 18)
lambdaQueryWrapper.gt(StudentEntity::getAge, 18)

// 大于等于
queryWrapper.ge("age", 18)
lambdaQueryWrapper.ge(StudentEntity::getAge, 18)

// 小于
queryWrapper.lt("age", 18)
lambdaQueryWrapper.lt(StudentEntity::getAge, 18)

// 小于等于
queryWrapper.le("age", 18)
lambdaQueryWrapper.le(StudentEntity::getAge, 18)

// between
queryWrapper.between("age", 18, 25)
lambdaQueryWrapper.between(StudentEntity::getAge, 18, 25)

// like
queryWrapper.like("name", "张")
lambdaQueryWrapper.like(StudentEntity::getName, "张")

// not like
queryWrapper.notLike("name", "张")
lambdaQueryWrapper.notLike(StudentEntity::getName, "张")
```

##### 范围操作
```java
// in
queryWrapper.in("name", "张三", "李四")
lambdaQueryWrapper.in(StudentEntity::getName, "张三", "李四")

// not in
queryWrapper.notIn("name", "张三", "李四")
lambdaQueryWrapper.notIn(StudentEntity::getName, "张三", "李四")

// is null
queryWrapper.isNull("name")
lambdaQueryWrapper.isNull(StudentEntity::getName)

// is not null
queryWrapper.isNotNull("name")
lambdaQueryWrapper.isNotNull(StudentEntity::getName)
```

##### 排序操作
```java
// 升序
queryWrapper.orderByAsc("age")
lambdaQueryWrapper.orderByAsc(StudentEntity::getAge)

// 降序
queryWrapper.orderByDesc("age")
lambdaQueryWrapper.orderByDesc(StudentEntity::getAge)

// 多字段排序
queryWrapper.orderByAsc("age").orderByDesc("name")
lambdaQueryWrapper.orderByAsc(StudentEntity::getAge).orderByDesc(StudentEntity::getName)
```

##### 分组和聚合操作
```java
// 分组
queryWrapper.groupBy("age")
lambdaQueryWrapper.groupBy(StudentEntity::getAge)

// having
queryWrapper.groupBy("age").having("count(*) > 1")
lambdaQueryWrapper.groupBy(StudentEntity::getAge).having("count(*) > 1")

// count
queryWrapper.count("id")
lambdaQueryWrapper.count(StudentEntity::getId)

// sum
queryWrapper.sum("score")
lambdaQueryWrapper.sum(StudentEntity::getScore)
```

#### 实际使用示例

##### 使用QueryWrapper（基于字符串）

```java
import com.hbasesoft.framework.db.core.BaseDao;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;

/**
 * 学生服务类 - 使用QueryWrapper示例
 */
@Service
public class StudentService {
    @Resource
    private IStudentDao studentDao;

    /**
     * 查询年龄在18-25岁之间的学生
     */
    public List<StudentEntity> getStudentsByAgeRange() {
        return studentDao.query(q ->
            q.ge("age", 18).le("age", 25).orderByAsc("age")
        );
    }

    /**
     * 查询姓名包含"张"的学生
     */
    public List<StudentEntity> getStudentsWithNameLike() {
        return studentDao.query(q ->
            q.like("name", "张").orderByAsc("name")
        );
    }

    /**
     * 查询特定ID列表的学生
     */
    public List<StudentEntity> getStudentsByIds(List<String> ids) {
        return studentDao.query(q ->
            q.in("id", ids.toArray())
        );
    }
}
```

##### 使用LambdaQueryWrapper（类型安全）

```java
import com.hbasesoft.framework.db.core.criteria.LambdaQueryWrapper;
import com.hbasesoft.framework.db.core.BaseDao;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;

/**
 * 学生服务类 - 使用LambdaQueryWrapper示例
 *
 * LambdaQueryWrapper的优势：
 * 1. 编译时类型检查 - 字段名拼写错误立即发现
 * 2. 重构友好 - IDE重命名字段时自动更新
 * 3. 代码可读性高 - 方法引用清晰表达查询意图
 */
@Service
public class StudentService {

    @Resource
    private IStudentDao studentDao;

    /**
     * 查询年龄大于18岁的学生（类型安全版本）
     *
     * 使用LambdaQueryWrapper，字段名通过方法引用获取
     * 如果StudentEntity没有getAge()方法，编译时会报错
     */
    public List<StudentEntity> getAdultStudents() {
        return studentDao.queryByLambda(q ->
            q.gt(StudentEntity::getAge, 18).orderByAsc(StudentEntity::getAge)
        );
    }

    /**
     * 查询姓名包含"张"的学生（类型安全版本）
     */
    public List<StudentEntity> getStudentsWithNameLike() {
        return studentDao.queryByLambda(q ->
            q.like(StudentEntity::getName, "张").orderByAsc(StudentEntity::getName)
        );
    }

    /**
     * 查询特定ID列表的学生（类型安全版本）
     */
    public List<StudentEntity> getStudentsByIds(List<String> ids) {
        return studentDao.queryByLambda(q ->
            q.in(StudentEntity::getId, ids.toArray(new String[0]))
        );
    }

    /**
     * 复杂查询示例：多条件组合
     *
     * 演示LambdaQueryWrapper的链式调用和条件组合
     */
    public List<StudentEntity> getStudentsWithComplexConditions() {
        return studentDao.queryByLambda(q ->
            q.gt(StudentEntity::getAge, 18)           // 年龄大于18
             .like(StudentEntity::getName, "张")       // 姓名包含"张"
             .in(StudentEntity::getGrade, "1", "2")    // 年级在1或2
             .orderByAsc(StudentEntity::getAge)        // 按年龄升序
             .orderByDesc(StudentEntity::getScore)     // 再按分数降序
        );
    }

    /**
     * 使用OR条件
     */
    public List<StudentEntity> getStudentsByNameOrAge() {
        return studentDao.queryByLambda(q ->
            q.or(w -> w.like(StudentEntity::getName, "张"))  // 姓名包含"张"
             .or(w -> w.gt(StudentEntity::getAge, 20))       // 或者年龄大于20
        );
    }
}
```

##### 分页查询示例

```java
/**
 * 分页查询学生
 */
public PagerList<StudentEntity> getStudentsByPage(int pageIndex, int pageSize) {
    return studentDao.queryPager(
        q -> q.orderByAsc(StudentEntity::getName),
        pageIndex,
        pageSize
    );
}
```

##### 聚合查询示例

```java
/**
 * 统计不同年龄段的学生数量
 *
 * 演示LambdaQueryWrapper的分组和聚合功能
 */
public List<StudentStatistics> getStudentStatisticsByAgeGroup() {
    return studentDao.queryByLambda(
        q -> q.select(StudentEntity::getAge)
              .groupBy(StudentEntity::getAge)
              .count(StudentEntity::getId, "count"),
        StudentStatistics.class
    );
}

/**
 * 统计每个班级的平均分
 */
public List<ClassStatistics> getClassAverageScore() {
    return studentDao.queryByLambda(
        q -> q.select(StudentEntity::getClassId)
              .groupBy(StudentEntity::getClassId)
              .avg(StudentEntity::getScore, ClassStatistics::getAverageScore),
        ClassStatistics.class
    );
}
```

#### UpdateWrapper使用示例

##### 使用UpdateWrapper（基于字符串）

```java
import com.hbasesoft.framework.db.core.criteria.UpdateWrapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;

/**
 * 学生服务类 - 使用UpdateWrapper示例
 */
@Service
public class StudentService {
    @Resource
    private IStudentDao studentDao;

    /**
     * 更新学生的年龄
     */
    @Transactional
    public void updateStudentAge(String id, int newAge) {
        studentDao.update(q ->
            q.set("age", newAge).eq("id", id)
        );
    }

    /**
     * 批量更新：将所有未及格学生的分数调整为60分
     */
    @Transactional
    public void updateFailedStudents() {
        studentDao.update(q ->
            q.set("score", 60).lt("score", 60)
        );
    }
}
```

##### 使用LambdaUpdateWrapper（类型安全）

```java
import com.hbasesoft.framework.db.core.criteria.LambdaUpdateWrapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;

/**
 * 学生服务类 - 使用LambdaUpdateWrapper示例
 */
@Service
public class StudentService {

    @Resource
    private IStudentDao studentDao;

    /**
     * 使用LambdaUpdateWrapper更新学生的年龄
     *
     * LambdaUpdateWrapper提供类型安全的字段引用
     */
    @Transactional
    public void updateStudentAgeWithLambda(String id, int newAge) {
        studentDao.updateByLambda(q ->
            q.set(StudentEntity::getAge, newAge).eq(StudentEntity::getId, id)
        );
    }

    /**
     * 批量更新：将所有未及格学生的分数调整为60分（类型安全版本）
     */
    @Transactional
    public void updateFailedStudentsWithLambda() {
        studentDao.updateByLambda(q ->
            q.set(StudentEntity::getScore, 60).lt(StudentEntity::getScore, 60)
        );
    }

    /**
     * 多字段更新
     */
    @Transactional
    public void updateStudentInfo(String id, String newName, Integer newAge) {
        studentDao.updateByLambda(q ->
            q.set(StudentEntity::getName, newName)
             .set(StudentEntity::getAge, newAge)
             .eq(StudentEntity::getId, id)
        );
    }
}
```

#### DeleteWrapper使用示例

##### 使用DeleteWrapper（基于字符串）

```java
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;

/**
 * 学生服务类 - 使用DeleteWrapper示例
 */
@Service
public class StudentService {
    @Resource
    private IStudentDao studentDao;

    /**
     * 删除特定年龄的学生
     */
    @Transactional
    public void deleteStudentsByAge(int age) {
        studentDao.delete(q ->
            q.eq("age", age)
        );
    }

    /**
     * 删除未及格的学生记录
     */
    @Transactional
    public void deleteFailedStudents() {
        studentDao.delete(q ->
            q.lt("score", 60)
        );
    }
}
```

##### 使用LambdaDeleteWrapper（类型安全）

```java
import com.hbasesoft.framework.db.core.criteria.LambdaDeleteWrapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;

/**
 * 学生服务类 - 使用LambdaDeleteWrapper示例
 */
@Service
public class StudentService {

    @Resource
    private IStudentDao studentDao;

    /**
     * 使用LambdaDeleteWrapper删除特定年龄的学生
     *
     * LambdaDeleteWrapper提供类型安全的字段引用
     */
    @Transactional
    public void deleteStudentsByAgeWithLambda(int age) {
        studentDao.deleteByLambda(q ->
            q.eq(StudentEntity::getAge, age)
        );
    }

    /**
     * 删除未及格的学生记录（类型安全版本）
     */
    @Transactional
    public void deleteFailedStudentsWithLambda() {
        studentDao.deleteByLambda(q ->
            q.lt(StudentEntity::getScore, 60)
        );
    }

    /**
     * 复杂条件删除
     */
    @Transactional
    public void deleteInactiveStudents() {
        studentDao.deleteByLambda(q ->
            q.lt(StudentEntity::getScore, 60)
             .lt(StudentEntity::getAge, 18)
        );
    }
}
```

#### 最佳实践

1. **优先使用LambdaQueryWrapper**
   - 新项目推荐使用LambdaQueryWrapper
   - 获得编译时类型检查和IDE支持
   - 便于重构和维护

2. **QueryWrapper适用场景**
   - 动态字段名场景
   - 字段名本身是变量
   - 需要拼接字符串的场景

3. **条件组合建议**
   - 使用链式调用提高可读性
   - 复杂逻辑使用or()拆分
   - 合理使用条件参数（condition）避免空指针

4. **性能优化**
   - 避免在循环中构建查询
   - 合理使用索引字段作为查询条件
   - 注意分页查询的性能

通过使用QueryWrapper系列类，开发者可以更直观、更安全地构造数据库查询条件，提高开发效率并减少错误。