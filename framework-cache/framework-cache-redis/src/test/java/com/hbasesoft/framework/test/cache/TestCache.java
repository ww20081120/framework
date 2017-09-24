/**
 * 
 */
package com.hbasesoft.framework.test.cache;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.hbasesoft.framework.boostrap.normal.Application;
import com.hbasesoft.framework.cache.core.CacheHelper;

/**
 * <Description> <br>
 * 
 * @author wangwei<br>
 * @version 1.0<br>
 * @CreateDate 2015年6月21日 <br>
 * @see com.hbasesoft.framework.test.cache <br>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
public class TestCache {

    public static class Person {
        private String name;

        private int age;

        public Person() {
        }

        /**
         * @param name
         * @param age
         */
        public Person(String name, int age) {
            super();
            this.name = name;
            this.age = age;
        }

        /**
         * Description: <br>
         * 
         * @author 王伟<br>
         * @taskId <br>
         * @return <br>
         */
        @Override
        public String toString() {
            return "Person [name=" + name + ", age=" + age + "]";
        }
    }

    /**
     * Description: putNode<br>
     * 
     * @author 王伟 <br>
     * @ <br>
     */
    @Before
    public void putNode() {
        Map<String, Person> map = new HashMap<String, Person>();
        map.put("zhangsan", new Person("张三", 23));
        map.put("lisi", new Person("李四", 24));
        map.put("wangwu", new Person("王五", 24));
        CacheHelper.getCache().putNode("person", map);
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @ <br>
     */
    @After
    public void getNode() {
        System.out.println(CacheHelper.getCache().getNode("person", Person.class));
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @ <br>
     */
    @Test
    public void getValue() {
        System.out.println(CacheHelper.getCache().get("person", "zhangsan", Person.class));
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @ <br>
     */
    @Test
    public void putValue() {
        CacheHelper.getCache().put("person", "liuliu", new Person("溜溜", 26));
    }

    /**
     * Description: updateValue<br>
     * 
     * @author 王伟 <br>
     * @ <br>
     */
    @Test
    public void updateValue() {
        CacheHelper.getCache().put("person", "liuliu", new Person("溜溜", 27));
    }

    /**
     * Description: removeValue<br>
     * 
     * @author 王伟 <br>
     * @ <br>
     */
    @Test
    public void removeValue() {
        CacheHelper.getCache().evict("person", "zhangsan");
    }

    /**
     * Description: removeNode<br>
     * 
     * @author 王伟 <br>
     * @ <br>
     */
    @Test
    public void removeNode() {
        CacheHelper.getCache().removeNode("person");
    }

    /**
     * Description: clean<br>
     * 
     * @author 王伟 <br>
     * @ <br>
     */
    @Test
    public void clean() {
        CacheHelper.getCache().clear();
    }
}
