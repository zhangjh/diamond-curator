## diamond-curator
一个不侵入业务的良好接入淘宝diamond的客户端

### 接入方式
1. 引入二方包依赖
    ```xml
    <dependency>
        <groupId>me.zhangjh</groupId>
        <artifactId>diamond-curator</artifactId>
        <version>1.0-SNAPSHOT</version>
    </dependency>
    ```
2. 业务使用场景定义动态配置类，打上@Diamond注解，提供要监听的diamond配置的groupId，dataId信息，同时要将该类声明为bean，如：
   ```java
    @Data
    @Diamond(groupId = "test.groupId", dataId = "test.dataId")
    @Component
    public class TestDiamondHolder {
    
        private Integer test;
    }
   ``` 
   注意类中声明的字段要和diamond配置中定义的key保持一致，@Diamond注解还包含format，split属性，这两个属性为可选属性，默认值分别为“text”和“：”。format代表配置项的格式。
   默认为text，设置JSON数据源时需指定format=json。split代表的是普通文本内容用以区分key，value的分隔符，默认为“：”，如果分隔符不为“：”时，需要传递该属性值。
3. 将监听器声明为bean

    将包路径me.zhangjh加入自动扫包，可以自动注册为bean，也可以手动指定：
   `<bean class="me.zhangjh.diamond.DiamondListener" />`
4. 业务使用
   跟使用普通bean一样，如：
   ```java
   @Autowired
   private TestDiamondHolder testDiamondHolder;
   
   public void test() {
        System.out.println(testDiamondHolder.getTest());
   }
   ```

注意：某些低版本Spring项目中，无法通过注解指定的变量完成替换，可以采用抽象类的方式，对业务稍有侵入。
1. 定义DiamondHolder，继承抽象类AbstractDiamondListener
2. 覆写抽象方法：initWithGroupIdAndDataId，设置groupId，dataId