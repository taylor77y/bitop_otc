package com.bitop.otcapi;

import org.apache.commons.lang3.ArrayUtils;
import org.jasypt.encryption.StringEncryptor;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DatabaseJasyptTest {

    @Autowired
    private StringEncryptor encryptor;

    @Test
    public void whenCalledhashCode_thenCorrect() {
        String[] array = {"a", "b", "c"};
        assertThat(ArrayUtils.hashCode(array))
                .isEqualTo(997619);
    }

    @Test
    public void whenCalledtoMap_thenCorrect() {
        String[][] array = {{"1", "one", }, {"2", "two", }, {"3", "three"}};
        Map map = new HashMap();
        map.put("1", "one");
        map.put("2", "two");
        map.put("3", "three");
        assertThat(ArrayUtils.toMap(array))
                .isEqualTo(map);
    }


/*    @Test
    public void getPass() {
        String url = encryptor.encrypt("jdbc:mysql://127.0.0.1:3306/bitop?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=Asia/Shanghai");
        String name = encryptor.encrypt("root");
        String password = encryptor.encrypt("root");
        System.out.println("database url: " + url);
        System.out.println("database name: " + name);
        System.out.println("database password: " + password);
        Assert.assertTrue(url.length() > 0);
        Assert.assertTrue(name.length() > 0);
        Assert.assertTrue(password.length() > 0);
    }*/
}
