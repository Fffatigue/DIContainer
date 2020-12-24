package ru.nsu.fit.g20221.DIContainer.impl;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.nsu.fit.g20221.DIContainer.ConfigurationReader;
import ru.nsu.fit.g20221.DIContainer.annotation.Bean;
import ru.nsu.fit.g20221.DIContainer.annotation.Component;
import ru.nsu.fit.g20221.DIContainer.annotation.Config;
import ru.nsu.fit.g20221.DIContainer.model.JavaObjectConfig;
import ru.nsu.fit.g20221.DIContainer.model.ScanObjectConfig;
import ru.nsu.fit.g20221.DIContainer.model.XMLConfigure;
import ru.nsu.fit.g20221.DIContainer.model.XmlObjectConfig;
import ru.nsu.fit.g20221.DIContainer.util.JaxbUtil;

public class ConfigurationReaderImpl implements ConfigurationReader {
    private static final Logger log = LoggerFactory.getLogger(ConfigurationReaderImpl.class);

    @Override
    public Collection<XmlObjectConfig> readConfigurationFromStream(InputStream stream) throws Exception {
        XMLConfigure xmlConfigure = JaxbUtil.unmarshall(XMLConfigure.class, stream);

        return xmlConfigure.getObjectConfig();
    }

    @Override
    public Collection<JavaObjectConfig> readConfigurationFromClass(Class clazz) {
        if (clazz.getAnnotation(Config.class) == null) {
            log.error("Class {} is not java configuration class", clazz.getCanonicalName());
            throw new RuntimeException("Class "+ clazz.getCanonicalName() + " is not java configuration class");
        };
        Reflections reflections = new Reflections(clazz, new MethodAnnotationsScanner());
        Set<Method> scannedMethods = reflections.getMethodsAnnotatedWith(Bean.class);
        List<JavaObjectConfig> scanObjectConfigs = new ArrayList<>();
        Constructor[] constructors = clazz.getConstructors();
        if (constructors.length > 1) {
            log.error("Java configuration class {} have more then one constructor", clazz.getCanonicalName());
            throw new RuntimeException("Java configuration class " + clazz.getCanonicalName() +" have more then one constructor");
        }
        Object configObject = null;
        try {
            configObject = constructors[0].newInstance();
        } catch (Exception e) {
            log.error("Can't create instance of java configuration class {}", clazz.getCanonicalName());
            throw new RuntimeException("Can't create instance of java configuration class " + clazz.getCanonicalName());
        }
        for (Method scannedMethod : scannedMethods) {
            scanObjectConfigs.add(new JavaObjectConfig(configObject, scannedMethod));
        }
        return scanObjectConfigs;
    }

    @Override
    public Collection<ScanObjectConfig> readConfigurationFromComponentScan() {
        Reflections reflections = new Reflections("", new TypeAnnotationsScanner(), new SubTypesScanner());
        Set<Class<?>> scannedClasses = reflections.getTypesAnnotatedWith(Component.class);
        List<ScanObjectConfig> scanObjectConfigs = new ArrayList<>();
        for (Class scannedClass : scannedClasses) {
            Constructor[] constructors = scannedClass.getConstructors();
            if (constructors.length > 1) {
                log.error("Component class {} have ore then one constructor", scannedClass.getCanonicalName());
                throw new RuntimeException("Component class " + scannedClass.getCanonicalName() + " have ore then one constructor");
            }
            Component annotation = (Component) scannedClass.getAnnotation(Component.class);
            scanObjectConfigs.add(new ScanObjectConfig(annotation.name(), constructors[0]));
        }
        return scanObjectConfigs;
    }
}
