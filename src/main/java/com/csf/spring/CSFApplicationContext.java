package com.csf.spring;

import java.io.File;
import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;

public class CSFApplicationContext {
    private Class configClass;

    private ConcurrentHashMap<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<String, BeanDefinition>();

    public CSFApplicationContext(Class configClass) {
        this.configClass = configClass;

        //扫描
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

                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }

                }
            }
            System.out.println(beanDefinitionMap.size());

        }
    }

    private String defaultComponentName(String className) {
        String name = className.substring(className.lastIndexOf(".") + 1);
        String pre = name.substring(0,1).toLowerCase();
        return pre + name.substring(1);

    }

    public Object getBean(String beanName) {
        return null;
    }
}
