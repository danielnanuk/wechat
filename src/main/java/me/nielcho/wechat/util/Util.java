package me.nielcho.wechat.util;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.function.Function;

@Component
public class Util implements ApplicationContextAware {
    private static ApplicationContext applicationContext;

    public static <T, U> U map(T t, Function<T, U> fun) {
        if (t == null) return null;
        return fun.apply(t);
    }

    @SuppressWarnings("unchecked")
    public static <T> T initializeBean(T bean) {
        AutowireCapableBeanFactory beanFactory = applicationContext.getAutowireCapableBeanFactory();
        beanFactory.autowireBean(bean);
        return (T) beanFactory.initializeBean(bean, bean.getClass().getName());
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Util.applicationContext = applicationContext;
    }

    // null safe get the first element in a collection
    public static <T> T firstOf(Collection<T> collection) {
        if (CollectionUtils.isEmpty(collection)) return null;
        for (T instance : collection) {
            if (instance != null) return instance;
        }
        return null;
    }
}
