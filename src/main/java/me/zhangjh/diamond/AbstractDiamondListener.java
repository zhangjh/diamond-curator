package me.zhangjh.diamond;

import com.taobao.diamond.manager.impl.DefaultDiamondManager;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.util.Map;

/**
 * @author zhangjh
 * @date 2022/04/21
 * 低版本spring项目使用注解方式无法传递变量，可选用该抽象类
 */
@Slf4j
@NoArgsConstructor
@Data
public abstract class AbstractDiamondListener implements InitializingBean {

    private String dataId;

    private String groupId;

    private String format = "text";

    private String split = ":";

    /**
     * 子类继承并实现，将变量groupId,dataId传递给类属性，没有默认实现是确保子类会覆写该初始化方法
     * */
    public abstract void initWithGroupIdAndDataId();

    @Autowired
    private ApplicationContext context;

    @Override
    public void afterPropertiesSet() {
        this.initWithGroupIdAndDataId();
        Validate.isTrue(StringUtils.isNotBlank(groupId), "groupId为空，请初始化");
        Validate.isTrue(StringUtils.isNotBlank(dataId), "dataId为空，请初始化");

        Map<String, AbstractDiamondListener> beans = context.getBeansOfType(AbstractDiamondListener.class);

        beans.forEach((name, bean) -> {
            // 找到跟配置关联的bean再处理
            if(bean.getGroupId() == null || bean.getDataId() == null) {
                return;
            }
            if(bean.getGroupId().equals(groupId) && bean.getDataId().equals(dataId)) {
                log.info("groupId: {}, dataId: {}", groupId, dataId);

                DefaultDiamondManager defaultDiamondManager = CommonLogic.getListener(groupId, dataId, format, split, bean);

                String config = defaultDiamondManager.getAvailableConfigureInfomation(10000);
                CommonLogic.handleConfig(config.trim(), format, split, bean);
            }
        });
    }
}
