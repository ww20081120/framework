Framework 3.0 框架说明
=======

# 更新说明
版本|更新内容| 时间|修改人
--- | --- | --- | ---
3.0 | 1.更新了spring boot 版本至2.0 <br> 2.去除dubbox框架，引入spring cloud框架 <br> 3. 重新定义了common 模块的API <br> 4. 支持JPA <br> 5. 支持yml格式的配置文件| 2017年9月24 | 王伟

# 框架介绍

Framework框架集成了log、cache、db、message，每块都以模块形式组织，可以根据项目需要获取模块。

+ framework-common 定义公用的常量、工具类 采用了spring-boot方式启动， 启动类为Application， 也可以支持web方式启动。
+ framework-log 分布式集成日志模块，详细的记录了每个方法执行的参数、返回结果、执行时间，可以很方便的排查问题或告警，通过远程接口上传服务器（支持直连服务端，也支持通过kafka发送）
+ framework-cache 定义了缓存的获取。  支持注解方式访问缓存， 支持基于Redis的分布式锁
+ [framework-db](#framework-db) 是简单易用的轻量级DAO(Data Access Object)框架，它集成了Hibernate实体维护和Mybaits SQL分离的两大优势，提供了非入侵式API，可以与Hibernate、SpringJdbc等数据库框架很好的集成 
+ framework-job 基于[ElasticJob](http://elasticjob.io)简单封装的定时器，支持分布式、分片等功能
+ framework-message 消息模块，通过简单的api发布和订阅事件， 目前支持kafka、redis、rocketMq
+ framework-rule 规则引擎，基于json的简单规则引擎， 支持多种插件及扩展， 例如：基于状态机的工作流引擎

-------
## <p id="framework-db">framework-db</p>具有以下特征:

1. O/R mapping不用设置xml，零配置便于维护  
2. 不需要了解JDBC的知识  
3. SQL语句和java代码的分离  
4. 可以自动生成SQL语句  
5. 接口和实现分离，不用写持久层代码，用户只需写接口，以及某些接口方法对应的sql 它会通过动态代理自动生成实现类  
6. 支持自动事务处理和手动事务处理  
7. MiniDao整合了Hibernate+mybatis的两大优势，支持实体维护和SQL分离  
8. SQL支持脚本语言  
9. 可以无缝集成Hibernate、Spring等第三方框架，也可以单独部署运行，适应性强。  

 
## 接口和SQL文件对应目录 
 
### 接口文件[EmpDao.java]

``` java
    @Dao
	public interface EmpDao {

	    @Sql("select * from emp")
	    List<Map<String,Object>> selectAll();
	    
	    @Sql("select * from emp where empno = :empno")
	    Map<String,Object> selectOne(@Param("empno") int empno);
	    
	    @Sql(value="select * from emp where deptno = :dept.deptno", bean=Emp.class)
	    List<Emp> selectDeptEmp(@Param("deptno")Dept dept, @Param(Param.pageIndex)int pageIndex,@Param(Param.pageSize)int pageSize);
	    
	    @Sql(bean = Emp.class)
	    List<Emp> queryEmp(@Param("dept") Dept dept);
	    
	    @Sql("select count(*) from emp")
	    int listCount(ResultTransformer transformer);
	}
	
```

### SQL文件[EmpDao_queryEmp.sql] 
``` sql 
	select * from emp 
	where 1=1 
	#if($dept) 
		and deptno=$dept.deptno 
	#end
```

### 测试代码[test.java]
``` java
	@RunWith(SpringJUnit4ClassRunner.class)
	@ContextConfiguration({
	    "classpath:/META-INF/spring/*.xml"
	})
	@Transactional
	public class EmpService {
	    @Resource
	    private EmpDao empDao;
	    @Test
	    public void test() {
	        try {
	            Dept dept = new Dept();
	            dept.setDeptno(30);
	            System.out.println("------------------");
	            System.out.println(empDao.queryEmp(dept).size());
	            System.out.println("------------------");
	        }
	        catch (Exception e) {
	            e.printStackTrace();
	        }
	    }
	 }
```

---
###　　　　　　　　　　　　Author:王伟
###　　　　　　　　　 E-mail:[ww20081120@126.com](mail://ww20081120@126.com)

