package com.p2p.demo.pojo;

import com.p2p.demo.util.JSONUtils;
import org.junit.Test;

public class ResultTest {

    @Test
    public void test_result_byte_null(){
        Result res = Result.ok("asdf",null);
        String str = JSONUtils.beanToJson(res);
        System.out.println(str);

        Result res2 = JSONUtils.jsonToBean(str,Result.class);
        System.out.println(res2.getCode());
        System.out.println(res2.getMessage());
        System.out.println(res2.getData());
    }
}