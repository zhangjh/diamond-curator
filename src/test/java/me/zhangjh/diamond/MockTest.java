package me.zhangjh.diamond;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author zhangjh
 * @date 2022/4/21
 */
public class MockTest {

    @Autowired
    private TestTextDiamondHolder textDiamondHolder;

    @Autowired
    private TestJsonDiamondHolder jsonDiamondHolder;

    public void test() {
        System.out.println(textDiamondHolder.getTestNum());

        System.out.println(jsonDiamondHolder.getTestJsonContent());
    }
}
