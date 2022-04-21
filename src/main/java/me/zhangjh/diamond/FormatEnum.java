package me.zhangjh.diamond;

import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

/**
 * @author zhangjh
 * @date 2022/04/21
 */
@Getter
public enum FormatEnum {

    /**
     * 普通文本格式
     * */
    TEXT("text"),
    /**
     * json格式
     * */
    JSON("json");

    private final String code;

    FormatEnum(String code) {
        this.code = code;
    }

    public static Optional<FormatEnum> getEnumByCode(String code) {
        return Arrays.stream(FormatEnum.values())
                .filter(value -> value.getCode().equalsIgnoreCase(code))
                .findFirst();
    }
}
