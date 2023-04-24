package com.csf.spring;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class CSFApplicationContext {
    private Class configClass;

    private ConcurrentHashMap<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<String, BeanDefinition>();
    private ConcurrentHashMap<String, Object> singletonMap = new ConcurrentHashMap<String, Object>();
    private List<BeanPostProcessor> beanPostProcessorList = new ArrayList<BeanPostProcessor>();

    public CSFApplicationContext(Class configClass) {
        this.configClass = configClass;

        //扫描 ---> BeanDefinition ---> beanDefinitionMap
        if (configClass.isAnnotationPresent(ComponentScan.class)) {
            ComponentScan componentScanAnnotation = (ComponentScan) configClass.getAnnotation(ComponentScan.class);
            String path = componentScanAnnotation.value();

            String filePath = path.replace(".", "/");

            ClassLoader classLoader = CSFApplicationContext.class.getClassLoader();
            URL resource = classLoader.getResource(filePath);
            String resourcePath = resource.getPath();
            File baseDir = new File(resourcePath);
            File[] files = baseDir.listFiles();

            for (File file : files) {
                String absolutePath = file.getAbsolutePath();
                if (absolutePath.endsWith(".class")) {
                    String s = absolutePath.replace(File.separator, ".");
                    String className = s.substring(s.lastIndexOf(path), s.lastIndexOf("."));
                    try {
                        Class<?> aClass = classLoader.loadClass(className);

                        if (aClass.isAnnotationPresent(Component.class)) {

                            if (BeanPostProcessor.class.isAssignableFrom(aClass)) {
                                BeanPostProcessor instance = (BeanPostProcessor) aClass.getConstructor().newInstance();
                                beanPostProcessorList.add(instance);
                            } else {
                                Component component = aClass.getAnnotation(Component.class);
                                String componentName = "".equals(component.value()) ? defaultComponentName(className) : component.value();
                                BeanDefinition beanDefinition = new BeanDefinition();
                                beanDefinition.setType(aClass);

                                if (aClass.isAnnotationPresent(Scope.class)) {
                                    Scope scopeAnnotation = aClass.getAnnotation(Scope.class);
                                    beanDefinition.setScope(scopeAnnotation.value());
                                } else {
                                    beanDefinition.setScope("singleton");
                                }
                                beanDefinitionMap.put(componentName, beanDefinition);
                            }
                        }

                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }

                }
            }

            // 实例化单例Bean
            for (String beanName : beanDefinitionMap.keySet()) {
                BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);

                if ("singleton".equals(beanDefinition.getScope())) {
                    if (!singletonMap.containsKey(beanName)) {
                        Object bean = createBean(beanName, beanDefinition);
                        singletonMap.put(beanName, bean);
                    }
                }
            }
        }
    }

    private Object createBean(String beanName, BeanDefinition beanDefinition) {
        Class aClass = beanDefinition.getType();
        try {
            Object instance = aClass.getConstructor().newInstance();
            for (Field f : aClass.getDeclaredFields()) {
                if (f.isAnnotationPresent(Autowired.class)) {
                    f.setAccessible(true);
                    f.set(instance, getBean(f.getName()));
                }
            }




            // Aware回调
            if (instance instanceof BeanNameAware) {
                ((BeanNameAware) instance).setBeanName(beanName);
            }

            for (BeanPostProcessor beanPostProcessor : beanPostProcessorList) {
                beanPostProcessor.postProcessBeforeInitialization(beanName, instance);
            }

            // 初始化
            if (instance instanceof InitializingBean) {
                ((InitializingBean) instance).afterPropertiesSet();
            }

            for (BeanPostProcessor beanPostProcessor : beanPostProcessorList) {
                instance = beanPostProcessor.postProcessAfterInitialization(beanName, instance);
            }

            return instance;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String defaultComponentName(String className) {
        String name = className.substring(className.lastIndexOf(".") + 1);
        String pre = name.substring(0,1).toLowerCase();
        return pre + name.substring(1);

    }

    public Object getBean(String beanName) {
        BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
        if (beanDefinition == null) {
            throw new NullPointerException();
        } else {
            String scope = beanDefinition.getScope();
            if ("singleton".equals(scope)) {
                Object bean = singletonMap.get(beanName);
                if (bean == null) {
                    bean = createBean(beanName, beanDefinition);
                    singletonMap.put(beanName, bean);
                }
                return bean;
            } else {
                return createBean(beanName, beanDefinition);
            }
        }
    }
}
