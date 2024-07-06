/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.common.utils.security;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import com.hbasesoft.framework.common.ErrorCodeDef;
import com.hbasesoft.framework.common.utils.Assert;

/**
 * <Description> 分词的测试类 <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2018年9月11日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.common <br>
 */
public class WordFilterTest {

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     *         <br>
     */
    @Test
    public void isHasFilterWord() {
        WordFilter filter = new WordFilter();
        Set<String> sss = new HashSet<String>();
        sss.add("南京");
        filter.init(sss);

        String str1 = "我家住在南京的天安门上";
        boolean b1 = filter.hasFilterWord(str1);
        Assert.isTrue(b1, ErrorCodeDef.FAILURE);
        System.out.println("字符串str1中包含关键字");

        String str2 = "我家住在北京的天安门上";
        boolean b2 = filter.hasFilterWord(str2);
        Assert.isFalse(b2, ErrorCodeDef.FAILURE);
        System.out.println("字符串str2中不包含关键字");
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     *         <br>
     */
    @Test
    public void getFilterString() {
        WordFilter filter = new WordFilter();
        Set<String> sss = new HashSet<String>();
        sss.add("南京");
        sss.add("北京");
        filter.init(sss);

        String str1 = "我家在南京，天安门在北京";
        String str2 = filter.getFilterString(str1, "*");
        System.out.println(str2);
        Assert.equals(str2, "我家在**，天安门在**", ErrorCodeDef.FAILURE);
        System.out.println("敏感字符已经被替换成*号了");

    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     *         <br>
     */
    @Test
    public void getKeyword() {
        WordFilter filter = new WordFilter();
        Set<String> sss = new HashSet<String>();

        sss.add("南京");
        sss.add("上海");
        sss.add("北京");
        sss.add("天津");
        sss.add("合肥");
        sss.add("深圳");
        filter.init(sss);

        String key = filter.getKeyword("我家住在南京的天安门上");
        Assert.equals(key, "南京", ErrorCodeDef.FAILURE);
        System.out.println("找到了\"南京\"这个关键词");
    }

}
