package com.fccfc.framework.bootstrap;

import javax.annotation.Resource;

import org.apache.thrift.TException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.fccfc.framework.config.api.ConfigService;

/**
 * <Description> <br>
 * 
 * @author wangwei<br>
 * @version 1.0<br>
 * @CreateDate 2015年6月27日 <br>
 * @see <br>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({
    "classpath:/META-INF/spring/*.xml"
})
public class ConfigServiceTest {

    @Resource
    private ConfigService.Iface configService;

    @Test
    public void queryAll() throws TException {
        System.out.println(configService.queryAllConfig("PORTAL"));
    }
}
