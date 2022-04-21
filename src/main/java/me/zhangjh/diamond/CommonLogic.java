package me.zhangjh.diamond;

import com.alibaba.fastjson.JSONObject;
import com.taobao.diamond.manager.ManagerListener;
import com.taobao.diamond.manager.impl.DefaultDiamondManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.springframework.beans.BeanUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.concurrent.Executor;

/**
 * @author zhangjh
 * @date 2022/04/21
 */
@Slf4j
public class CommonLogic {

    @SuppressWarnings("unchecked")
    public static <T> void handleConfig(String config, String format, String split, T bean) {
        log.info("handle diamond config start");
        try {
            Class<?> clazz = bean.getClass();

            Optional<FormatEnum> formatEnum = FormatEnum.getEnumByCode(format);

            if(formatEnum.isPresent()) {
                switch (formatEnum.get()) {
                    case JSON:
                        BeanUtils.copyProperties(JSONObject.parseObject(config, clazz), bean);
                        break;
                    case TEXT:
                        String[] lines = config.split("\n");
                        for (String line : lines) {
                            if(StringUtils.isEmpty(line)) {
                                continue;
                            }
                            String[] arr = line.split(split);
                            Validate.isTrue(arr.length >= 2, "配置内容格式有误");
                            // 第一个分隔符前的认为是key，剩下的部分认为是value
                            int pos = line.indexOf(split);
                            String key = line.substring(0, pos);
                            String value = line.substring(pos + 1);
                            // 给配置类字段反射赋值
                            try {
                                clazz.getDeclaredField(key);
                            } catch (NoSuchFieldException ignored) {
                                try {
                                    clazz.getSuperclass().getDeclaredField(key);
                                } catch (NoSuchFieldException ex) {
                                    // 如果从自身类未获取到字段，从父类继承的也获取不到，报错处理
                                    // diamond配置内容里包含了配置类没有配置的字段，记录日志方便排查问题，但不阻断
                                    log.error("diamond content contains field not defined, field: {}", key);
                                    continue;
                                }
                            }
                            PropertyDescriptor descriptor = new PropertyDescriptor(key, clazz);
                            Method writeMethod = descriptor.getWriteMethod();
                            writeMethod.setAccessible(true);

                            // 给字段赋值，支持动态类型转换，diamond配置均为String，可在diamondHolder中定义需要的类型
                            writeMethod.invoke(bean, ConvertUtils.convert(value, descriptor.getPropertyType()));
                        }
                        break;
                    default:
                        // 不会进入该分支，为了代码规范增加default
                        break;
                }
            } else {
                throw new RuntimeException("invalid content format, format: " + format);
            }
        } catch (Throwable t) {
            log.error("handleConfig exception, plz check. Throwable: ", t);
            throw new RuntimeException(t);
        }
    }

    public static <T> DefaultDiamondManager getListener(String groupId, String dataId, String format, String split, T bean) {
        return new DefaultDiamondManager(groupId, dataId, new ManagerListener() {
            @Override
            public Executor getExecutor() {
                return null;
            }

            @Override
            public void receiveConfigInfo(String config) {
                if(StringUtils.isEmpty(config)) {
                    return;
                }
                CommonLogic.handleConfig(config.trim(), format, split, bean);
            }
        });
    }
}
