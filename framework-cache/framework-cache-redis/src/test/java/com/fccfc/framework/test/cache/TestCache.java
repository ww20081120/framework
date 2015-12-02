/**
 * 
 */
package com.fccfc.framework.test.cache;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.fccfc.framework.cache.core.CacheException;
import com.fccfc.framework.cache.core.CacheHelper;

/**
 * <Description> <br>
 * 
 * @author wangwei<br>
 * @version 1.0<br>
 * @CreateDate 2015年6月21日 <br>
 * @see com.fccfc.framework.test.cache <br>
 */
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
     * @throws CacheException <br>
     */
    @Before
    public void putNode() throws CacheException {
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
     * @throws CacheException <br>
     */
    @After
    public void getNode() throws CacheException {
        System.out.println(CacheHelper.getCache().getNode(Person.class, "person"));
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @throws CacheException <br>
     */
    @Test
    public void getValue() throws CacheException {
        System.out.println(CacheHelper.getCache().getValue(Person.class, "person", "zhangsan"));
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @throws CacheException <br>
     */
    @Test
    public void putValue() throws CacheException {
        CacheHelper.getCache().putValue("person", "liuliu", new Person("溜溜", 26));
    }

    /**
     * Description: updateValue<br>
     * 
     * @author 王伟 <br>
     * @throws CacheException <br>
     */
    @Test
    public void updateValue() throws CacheException {
        CacheHelper.getCache().updateValue("person", "liuliu", new Person("溜溜", 27));
    }

    /**
     * Description: removeValue<br>
     * 
     * @author 王伟 <br>
     * @throws CacheException <br>
     */
    @Test
    public void removeValue() throws CacheException {
        CacheHelper.getCache().removeValue("person", "zhangsan");
    }

    /**
     * Description: removeNode<br>
     * 
     * @author 王伟 <br>
     * @throws CacheException <br>
     */
    @Test
    public void removeNode() throws CacheException {
        CacheHelper.getCache().removeNode("person");
    }

    /**
     * Description: clean<br>
     * 
     * @author 王伟 <br>
     * @throws CacheException <br>
     */
    @Test
    public void clean() throws CacheException {
        CacheHelper.getCache().clean();
    }
}
