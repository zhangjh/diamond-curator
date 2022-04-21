package me.zhangjh.diamond;

import lombok.Data;
import org.springframework.stereotype.Component;

/**
 * @author zhangjh
 * @date 2022/4/21
 * 文本型配置使用示例
 */
@Data
@Diamond(groupId = "xxx", dataId = "xxx")
@Component
public class TestTextDiamondHolder {

    /**
     * 对应diamond配置示例：
     *      testNum:123,
     *      testStr:"abcdefg"
     * */
    private Integer testNum;

    private String testStr;
}
