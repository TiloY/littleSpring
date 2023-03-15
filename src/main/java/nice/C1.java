package nice;

/**
 * @Description :
 * @Author : 田迎
 * @Date : 2022/7/22 18:42
 * @Version : 1.0.0
 **/
public class C1 {

    public static void main(String[] args) {

        // 接口 ？？？
        // 接口继承 ？？？
        /**
         * 每个接口都是不同的功能
         * 当多个接口组成在一起的时候，这个接口就具有其他接口的的性质
         *
         * // 接口
         * 1. 短剑 2. 暴风剑 3. 黄刀  5 吸血鬼权杖  8 攻速刀
         * 4 实现 1 2 ==》 无尽
         * 6 实现 2 5 ==》 饮血
         * 9 实现 3 8 1 ==》红叉
         *
         * ===============================================================
         * 注册中心：就是保存信息的地方 ，其他组件统一获取
         * ===============================================================
         分布式系统的session 都是保存在 redis 集群里边的
         *
         * ===============================================================
         * 什么是DI 什么是AOP 什么是IOC  ===》 实现原理是什么 ？
         * DI 就是我要啥它给我依赖注入进来 。
         * IOC 和DI 是一个概念 ， 以前我new 现在他给我依赖注入进来 set 或者构造器
         * AOP: 面向切面编程， 一个点切多个面 一个面切多个点
         *
         * 实现原理：反射+动态代理==》 在Spring中如何实现？
         * 在那张架构图里边的
         * 那一层？BeanPostProccesor 运行时增强
         *
         * ==============================================================
         * 运行时增强
         * 编译器增强 aspectJ lombok
         * 载入时增强
         *
         * ==============================================================
         * listableBeanFactory 与 HierarchicalBeanFactory 关系
         * 寻找Bean的时候，从子容器里拿，拿不到去父工厂里边拿
         * 使用场景：
         * springCloud-openfeign
         *
         * FactoryBean 也是一个实例Bean 也会走 BeanFactory 这一层的  BeanPostProcessor
         *
         * 经典面试题：Spring容器和SpringMVC容器是什么关系 ？
         * 父子容器关系 。 父容器不能访问子容器的，子容器可以访问父容器的
         * 每个接口都是不同的容器
         * 对于service 层 dao层 需要放到 spring 容器层 ，对于servlet Controller 放到 springMvc容器里边
         *
         * =====================================================================
         * 如果在项目中请求第三方的HTTP接口
         *  DefaultHttpClient.default();
         *  自己封装一层调用逻辑（请求方法 地址 参数 ）
         *
         *  feign :
         * @FeignClient("服务注册地址")
         * public interface IuserSerivice{
         * @RequestMapping("/mapping"){
         *   User getByUsetId(@param("id")Interger id);
         *  }
         * }
         * public class UserController{
         *
         * @Autoworied
         * private IuserSerivice userserice ;
         * }
         *
         * 这个东西 怎么做的 ？ 怎么注入的？  谁生产的？
         *
         * 1. 扫描路径下的class 获取 FeignClient 声明的类
         * 2. 反射获取 地址 路径 参数信息
         * 3. 通过FactoryBean提供实例化对象
         *
         *  Class clazz = </>IuserSerivice.class
         *  value = clazz。getAnnotation(FeignClient.class).value();
         *  clazz.getMethods();
         *  迭代获取requestMapping信息和参数信息 。
         *
         *  BeanPostProcess 的实际例子
         *
         *  =======================父子容器的实际例子  =====================
         *  为什么我的容器没有这个Bean的情况下 我要把放进去
         *  覆写  、
         *
         *  FeignContext
         */

        //====================第三节课
        /**
         1. BeanDefinationReader
         2. BeanFactory
         3. Eviroment
         4. BeanFactoryPostProcessor
         5. BeanPostProcessor
         6. FactoryBean



         */

        /**
         * 说说java的基础数据类型 占几个字节 ？
         *  9 个
         *  byte 1 short 2 int 4 long 8
         *  float 4 double 8
         *  boolean 1
         *  char 2
         *  returnAddress JVM 内部来使用的
         *  1 2 4 8
         */

        /** JVM 规范 手册
         *
         *  bean 生命周期 ？？？？
         *  XXXAware
         *  如果
         *
         */

        /**
         *  公平锁 和 非公平锁？
         *  线程饥饿
         *  如何解决 ？ 在获取读书的时候判断 当前是否有些锁排队
         *
         *  如DIO(Direct I/O)，AIO(Asynchronous I/O，异步I/O)，Memory-Mapped I/O(内存映射I/O)等，
         *  不同的I/O方式有不同的实现方式和性能，在不同的应用中可以按情况选择不同的I/O方式
          */

        /**
         * 1. 如何获取线程的返回值
         * 2. 10个线程执行完了之后再去执行一个线程
         * 3. 如何终止一个线程
         * 4. @transactional注解  同一个方法 同一个事务里边 有一块代码片段 ， 一个insert  一个 update 方法
         * 执行到 update 的时候 报错了  但是没有进行事务的回滚 导致这种情况发生的可能性有哪些 ？（单线环境）
         * 5. 我的程序在正常的运行过程中突然卡顿了 ，卡顿个1-2秒 ，又正常了 他可能过了一段时间又卡住了，又正常了 ，
         * 可能过了一段时间又卡顿了 反复的出现这种的情况 ，那出现了几次以后呢 我的整个程序就OOM了 ，问导致这种情况发生的
         * 可能性？
         * 6. rabitMQ 和  RocketMQ 的 区别 ？
         * 7. 消息在消费者端 重试之后还是消费失败 ，  这个消息是包含是业务数据， 你会做什么样的处理
         * 8.什么情况下会导致索引失效
         */




























      /**
         *
         *  @transactional注解 同一个方法 同一个事务里边 有一块代码片段 ， 一个insert  一个 update 方法
         *          * 执行到 update 的时候 报错了  但是没有进行事务的回滚 导致这种情况发生的可能性有哪些 ？（单线环境）
         */


    }
}
