package me.zhangjh.diamond;

import lombok.Data;
import org.springframework.stereotype.Component;

/**
 * @author zhangjh
 * @date 2022/4/21
 */
@Data
@Diamond(groupId = "xxx", dataId = "xxx", format = "json")
@Component
public class TestJsonDiamondHolder {

    private TestJsonContent testJsonContent;
}
