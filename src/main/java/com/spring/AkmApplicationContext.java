package com.spring;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class AkmApplicationContext {

    //配置类
    private Class configClass;

    private Map<String, BeanDefinition> beanDefinitionMap = new HashMap<>();

    private Map<String, Object> singletonObjects = new HashMap<>();

    public AkmApplicationContext(Class configClass) {
        this.configClass = configClass;

        // 1. 扫描需要创建bean的类，将类信息存入到BeanDefinition中
        scanConfigClass(configClass);

        // 2. 创建Bean实例
        for (Map.Entry<String, BeanDefinition> beanDefinitionEntry : beanDefinitionMap.entrySet()) {
            String beanName = beanDefinitionEntry.getKey();
            BeanDefinition beanDefinition = beanDefinitionEntry.getValue();
            if ("singleton".equals(beanDefinition.getScope())) {
                //如果是单例scope，就去创建bean实例
                Object bean = createBean(beanName, beanDefinition);
                singletonObjects.put(beanName, bean);
            }
        }
    }

    private Object createBean(String beanName, BeanDefinition beanDefinition) {
        Class clazz = beanDefinition.getType();

        Object instance = null;
        try {
            //根据类型创建bean的实例（默认无参构造）
            instance = clazz.getConstructor().newInstance();
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        return instance;
    }

    public Object getBean(String beanName) {

        if (!beanDefinitionMap.containsKey(beanName)) {
            //如果beanDefinitionMap没有这个beanNameKey
            throw new NullPointerException();
        }

        BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
        if("singleton".equals(beanDefinition.getScope())){
            //单例
            Object singletonBean = singletonObjects.get(beanName);
            return singletonBean;
        }else {
            //原型
            Object protoTypeBean = createBean(beanName, beanDefinition);
            return protoTypeBean;
        }

    }

    private void scanConfigClass(Class configClass) {
        if (configClass.isAnnotationPresent(ComponentScan.class)) {
            ComponentScan componentScanAnnotation = (ComponentScan) configClass.getAnnotation(ComponentScan.class);
            //获取当前扫描类指定的包名
            String path = componentScanAnnotation.value();
            //将.替换成/，获取target目录里面类的相对路径
            path = path.replace(".", "/");

            ClassLoader classLoader = AkmApplicationContext.class.getClassLoader();
            URL resource = classLoader.getResource(path);

            File file = new File(resource.getFile());

            if (file.isDirectory()) {
                for (File f : file.listFiles()) {
                    /*
                    根据扫描配置类的包名得到这个包名下每个类的绝对路径
                    例如：D:\code\akm-spring\akm-spring\target\classes\com\akm\service\UserService.class
                     */
                    String absolutePath = f.getAbsolutePath();
                    String componentClassPath = absolutePath.substring(absolutePath.indexOf("com"), absolutePath.indexOf(".class"));
                    componentClassPath = componentClassPath.replace("\\", ".");
                    System.out.println(componentClassPath);
                    Class<?> clazz = null;
                    try {
                        clazz = classLoader.loadClass(componentClassPath);

                        if (clazz.isAnnotationPresent(Component.class)) {

                            Component componentAnnotation = clazz.getAnnotation(Component.class);
                            String beanName = componentAnnotation.value();

                            //创建Bean的定义
                            BeanDefinition beanDefinition = new BeanDefinition();
                            beanDefinition.setType(clazz);

                            //找到需要创建单例对象的Bean（非懒加载型的对象）
                            if (clazz.isAnnotationPresent(Scope.class)) {
                                //多例bean
                                Scope scopeAnnotation = clazz.getAnnotation(Scope.class);
                                String value = scopeAnnotation.value();
                                //判断value是单例还是原型
                                beanDefinition.setScope(value);
                            } else{
                                //单例bean
                                beanDefinition.setScope("singleton");
                            }

                            beanDefinitionMap.put(beanName, beanDefinition);
                        }
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }

                }
            }
        }
    }
}
