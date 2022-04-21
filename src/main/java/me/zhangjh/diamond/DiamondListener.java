package me.zhangjh.diamond;

import com.taobao.diamond.manager.impl.DefaultDiamondManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.PropertyResolver;
import org.springframework.stereotype.Component;

import java.util.Map;


/**
 * @author zhangjh
 * @date 2022/04/21
 * Spring启动完成后执行，拦截@Diamond注解，注册diamond监听器，并自动将配置转为业务可识别内容
 * 需将该类声明为bean，业务定义的diamondHolder也需声明为bean
 */
@Slf4j
@Component
public class DiamondListener implements InitializingBean {

    private static final String SYMBOL = "$";

    @Autowired
    private ApplicationContext context;

    @Override
    public void afterPropertiesSet() {
        Map<String, Object> beans = context.getBeansWithAnnotation(Diamond.class);

        beans.forEach((name, bean) -> {
            Diamond annotation = bean.getClass().getAnnotation(Diamond.class);
            String groupId = annotation.groupId();
            String dataId = annotation.dataId();
            String format = annotation.format();
            String split = annotation.split();

            PropertyResolver propertyResolver = context.getEnvironment();
            String realGroupId = groupId;
            if(realGroupId.contains(SYMBOL)) {
                realGroupId = propertyResolver.resolvePlaceholders(groupId);
            }
            String realDataId = dataId;
            if(realDataId.contains(SYMBOL)) {
                realDataId = propertyResolver.resolvePlaceholders(dataId);
            }
            String realFormat = format;
            if(realFormat.contains(SYMBOL)) {
                realFormat = propertyResolver.resolvePlaceholders(format);
            }
            String realSplit = split;
            if(realSplit.contains(SYMBOL)) {
                realSplit = propertyResolver.resolvePlaceholders(split);
            }

            log.info("realGroupId: {}, realDataId: {}, realFormat: {}, realSplit: {}",
                    realGroupId, realDataId, realFormat, realSplit);

            DefaultDiamondManager defaultDiamondManager = CommonLogic.getListener(realGroupId, realDataId, realFormat, realSplit, bean);

            String config = defaultDiamondManager.getAvailableConfigureInfomation(10000);
            CommonLogic.handleConfig(config.trim(), realFormat, realSplit, bean);
        });
    }
}
