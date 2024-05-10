Framework 4.X 框架说明
=======

# 更新说明
版本|更新内容| 时间|修改人
--- | --- | --- | ---
4.0 | 使用jdk17版本，spring cloud使用2021.0.4版本| 2022年9月20 | 王伟
4.1 | 使用jdk21版本，spring boot 使用3.2版本，spring cloud使用2023.0.1版本| 2024年3月31 | 王伟

# 框架介绍
Framework框架集成了log、cache、db、message、rule、tx，每块都以模块形式组织，可以根据项目需要获取模块。
+ [framework-common](https://github.com/ww20081120/framework/wiki/%E5%9F%BA%E7%A1%80%E5%B7%A5%E5%85%B7) 定义公用的常量、工具类 采用了spring-boot方式启动， 启动类为Application， 也可以支持web方式启动。
+ [framework-log](https://github.com/ww20081120/framework/wiki/%E8%B7%9F%E8%B8%AA%E6%97%A5%E5%BF%97) 分布式集成日志模块，详细的记录了每个方法执行的参数、返回结果、执行时间，可以很方便的排查问题或告警，通过远程接口上传服务器（支持直连服务端，也支持通过kafka发送）
+ [framework-cache](https://github.com/ww20081120/framework/wiki/%E7%BC%93%E5%AD%98) 定义了缓存的获取。  支持注解方式访问缓存， 支持基于Redis的分布式锁
+ [framework-db](https://github.com/ww20081120/framework/wiki/%E6%95%B0%E6%8D%AE%E5%BA%93) 是简单易用的轻量级DAO(Data Access Object)框架，它集成了Hibernate实体维护和Mybaits SQL分离的两大优势，提供了非入侵式API，可以与Hibernate、SpringJdbc等数据库框架很好的集成 
+ [framework-job](https://github.com/ww20081120/framework/wiki/%E4%BB%BB%E5%8A%A1) 定时任务，支持quartz、xxl-job、[ElasticJob](http://elasticjob.io)简单封装的定时器，支持分布式、分片等功能
+ [framework-message](https://github.com/ww20081120/framework/wiki/%E5%BC%82%E6%AD%A5%E6%B6%88%E6%81%AF) 消息模块，通过简单的api发布和订阅事件， 目前支持kafka、redis、rocketMq
+ [framework-rule](https://github.com/ww20081120/framework/wiki/%E8%A7%84%E5%88%99%E5%BC%95%E6%93%8E) 规则引擎，基于json的轻量级规则引擎， 支持多种插件及扩展， 例如：基于状态机的工作流引擎
+ [framework-tx](https://github.com/ww20081120/framework/wiki/%E5%88%86%E5%B8%83%E5%BC%8F%E4%BA%8B%E5%8A%A1) 分布式事务，支持各种远程接口、同步异步消息。
+ [framework-dependencies] 项目依赖，解决版本包依赖问题
+ [framework-shell] 控制台方式提供命令操作，支持自定义各种命令，做各种小工具使用。
+ [framework-langchain4j] 对langchain4j的补充，支持国内的大模型，让大家更高效的开发AIGC应用。

>> jdk1.8请使用framework3.X版本，framework4.X已升级至jdk21版本

## <p id="framework-db">framework-db</p>具有以下特征:

1. O/R mapping不用设置xml，零配置便于维护  
2. 不需要了解JDBC的知识  
3. SQL语句和java代码的分离  
4. 可以自动生成SQL语句  
5. 接口和实现分离，不用写持久层代码，用户只需写接口，以及某些接口方法对应的sql 它会通过动态代理自动生成实现类  
6. 支持自动事务处理和手动事务处理  
7. Dao整合了Hibernate+mybatis的两大优势，支持实体维护和SQL分离  
8. SQL支持脚本语言  
9. 可以无缝集成Hibernate、Spring等第三方框架，也可以单独部署运行，适应性强。
10. 支持JPA的CriteriaQuery API，并且提供了类似于Mybatis plus中 QueryWapper和LambdaQueryWapper的这种易用的写法。

 
## 接口和SQL文件对应目录 
 
### 接口文件[EmpDao.java]

``` java
    @Dao
    public interface IStudentDao extends IBaseDao<StudentEntity>  {

	    @Sql("select count(1) from t_student_course sc, t_course c "
            + "where sc.course_id = c.id and sc.score >= 60 and c.course_name = :courseName")
        int countCoursePass(@Param("courseName") String courseName);
        
        @Sql
        List<StudentEntity> queryStudentCourse(@Param("entity") StudentEntity studentEntity,
        @Param(Param.PAGE_INDEX) int pageIndex, @Param(Param.PAGE_SIZE) int pageSize);

	}
	
```

### SQL文件[IStudentDao_queryStudentCourse.sql] 
``` sql 
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

```

### 测试代码[test.java]
``` java
	@SpringBootTest()
	@Transactional
	public class BaseDaoTester {
	
	    @Resource
	    private IStudentDao iStudentDao;
	    
	    @Test
	    public void countCourse() { // 统计
	        CriteriaBuilder cb = iCourseDao.criteriaBuilder();
	        CriteriaQuery<Long> query = cb.createQuery(Long.class);
	        Root<CourseEntity> root = query.from(CourseEntity.class);
	        query.select(cb.count(root));
	        Long count = iCourseDao.getByCriteria(query);
	        Assert.isTrue(count.intValue() == 3, ErrorCodeDef.SYSTEM_ERROR);
	    }
	    
	    @Test
	    public void queryStudentCourse() { // 复杂的SQL查询
	        List<StudentEntity> entityes = iStudentDao.queryStudentCourse(null, 1, 5);
	        Assert.isTrue(entityes.size() == 5, ErrorCodeDef.SYSTEM_ERROR);
	
	        entityes = iStudentDao.queryStudentCourse(null, 1, 3);
	        Assert.isTrue(entityes.size() == 3, ErrorCodeDef.SYSTEM_ERROR);
	
	        StudentEntity entity = new StudentEntity();
	        entity.setAge(19);
	        entityes = iStudentDao.queryStudentCourse(entity, 1, 10);
	        Assert.isTrue(entityes.size() == NUM_3, ErrorCodeDef.SYSTEM_ERROR);
	
	        entity = new StudentEntity();
	        entity.setAge(18);
	        entity.setName("张%");
	        entityes = iStudentDao.queryStudentCourse(entity, 1, 10);
	        Assert.isTrue(entityes.size() == NUM_3, ErrorCodeDef.SYSTEM_ERROR);
	    }
		
		@Test
	    public void queryBySpecification() { // JPA CriteriaQuery 用法
	        List<StudentEntity> es1 = iStudentDao.queryBySpecification((root, query, cb) -> {
	            return query.where(cb.equal(root.get(StudentEntity.AGE), 18)).getRestriction();
	        });
	
	        List<StudentEntity> es2 = iStudentDao.queryByProperty(StudentEntity.AGE, 18);
	        Assert.isTrue(es1.size() == es2.size(), ErrorCodeDef.SYSTEM_ERROR);
	    }
	    
	    @Test
	    @Transactional
	    public void query() { // QueryWapper 写法
	        List<StudentEntity> es1 = iStudentDao.query(q -> q.eq("age", 18).build());
	        Assert.isTrue(es1.size() == 2, ErrorCodeDef.SYSTEM_ERROR);
	    }
	    
	    @Test
	    @Transactional
	    public void queryByLambdaQuery() { // LambdaQueryWapper 写法
	        List<StudentEntity> es1 = iStudentDao.queryByLambda(q -> q.eq(StudentEntity::getAge, 18).build());
	        Assert.isTrue(es1.size() == 2, ErrorCodeDef.SYSTEM_ERROR);
	    }
	 }
```

## <p id="framework-tx">framework-tx</p>分布式事务:

实际生产过程中因为硬件故障、网络故障、依赖第三方系统故障给我们带来了很多麻烦。原来的重试补偿功能都是写在各业务模块，增加了很多额外的开发工作量，在加上开发人员水平也不一致也很难全面的考虑各种稳定性问题，所以研发出该模块用于解决微服务业务模块不稳定问题。因业务要求，不允许出现失败回滚场景，该模块只实现了事务补偿。 原理：通过N次重试，跳过执行成功的部分，一直重试失败部分，来达到业务最终执行完成。（N次失败后可以通知人工来进行解决）。

实际场景举例： 用户购买了商品，当微信支付成功后，突然订单模块数据库宕机了。 当数据库修复后，之前丢失的订单能正确处理。

#### 具有以下特征:

1. 支持同步消息与异步消息  
2. 提供注解方法，使用简单，学习成本低 
3. 任何需要重试的内容都可以使用，适应性强。  

#### 测试代码[TestProducter.java]

``` java
@RestController
public class TestProducter {

    @Resource
    private FeginClient2Consumer feClient2Consumer;

    @GetMapping
    @Tx
    public String test(@RequestParam("id") String id) {

        String value1 = TxInvokerProxy.invoke("client2", () -> {
            return feClient2Consumer.test(id);
        });
        System.out.println(value1);
        return value1;
    }
}

```

[---> 详细文档点击这里 <---](https://github.com/ww20081120/framework/wiki)

---
- Author:王伟 
- E-mail:[ww20081120@126.com](mail://ww20081120@126.com)

