package com.zhl.spring;

import com.zhl.annotation.RpcReference;
import com.zhl.annotation.RpcService;
import com.zhl.config.RpcServiceConfig;
import com.zhl.enums.RpcRequestTransportEnum;
import com.zhl.extensions.ExtensionLoader;
import com.zhl.factory.SingletonFactory;
import com.zhl.provider.ServiceProvider;
import com.zhl.provider.zk.ZkServiceProviderImpl;
import com.zhl.proxy.RpcClientProxy;
import com.zhl.remoting.transport.RpcRequestTransport;
import com.zhl.remoting.transport.netty.client.NettyRpcClient;
import io.protostuff.Rpc;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

/**
 * <p>
 * 在初始化前为 @RpcService 修饰的类注册服务
 * 在初始化后为 @RpcReference 修饰的属性，通过代理提供服务
 *
 * @author zhl
 * @since 2024-07-19 14:05
 */
@Slf4j
@Component
public class SpringBeanPostProcessors implements BeanPostProcessor {

    private final RpcRequestTransport rpcClient;
    private final ServiceProvider serviceProvider;

    public SpringBeanPostProcessors() {
        this.rpcClient = ExtensionLoader.getExtensionLoader(RpcRequestTransport.class)
            .getExtension(RpcRequestTransportEnum.NETTY.getName());
        this.serviceProvider = SingletonFactory.getInstance(ZkServiceProviderImpl.class);
    }

    /**
     * 为初始化前的bean (被@RpcService修饰的类)，发布服务
     * TODO: 可能在初始化前服务已经注册，但是有服务端调用，此时服务提供者还没有初始化完成，造成服务消费者没有找到指定的服务
     * @param bean     提供的服务
     * @param beanName the name of the bean
     */
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean.getClass().isAnnotationPresent(RpcService.class)) {
            log.info("[{}] is annotated with  [{}]", bean.getClass().getName(), RpcService.class.getCanonicalName());
            RpcService rpcService = bean.getClass().getAnnotation(RpcService.class);
            RpcServiceConfig rpcServiceConfig = RpcServiceConfig.builder()
                .group(rpcService.group())
                .version(rpcService.version())
                .service(bean)
                .build();
            serviceProvider.publishService(rpcServiceConfig);
        }
        return bean;
    }

    /**
     *  通过反射为客户端依赖的服务创建代理
     *  获取当前类中属性被 @RpcReference 修饰的，并通过动态代理提供服务
     * @param bean 客户端调用服务的属性外部类
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Field[] declaredFields = bean.getClass().getDeclaredFields();
        for (Field field : declaredFields) {
            RpcReference reference = field.getAnnotation(RpcReference.class);
            if (reference != null) {
                RpcServiceConfig rpcServiceConfig = RpcServiceConfig.builder()
                    .version(reference.version())
                    .group(reference.group())
                    .build();
                RpcClientProxy rpcClientProxy = new RpcClientProxy(rpcClient, rpcServiceConfig);
                // 根据属性的类型，通过动态代理获得服务
                Object value = rpcClientProxy.getProxy(field.getType());
                // 不受权限修饰符的限制可反射直接赋值
                field.setAccessible(true);
                try {
                    // 给目标类中调用服务的属性赋值（提供服务）
                    field.set(bean, value);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return bean;
    }
}
