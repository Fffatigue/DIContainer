package ru.nsu.fit.g20221.DIContainer.impl;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import ru.nsu.fit.g20221.DIContainer.ConfigurationReader;
import ru.nsu.fit.g20221.DIContainer.annotation.Component;
import ru.nsu.fit.g20221.DIContainer.model.JavaObjectConfig;
import ru.nsu.fit.g20221.DIContainer.model.ScanObjectConfig;
import ru.nsu.fit.g20221.DIContainer.model.XMLConfigure;
import ru.nsu.fit.g20221.DIContainer.model.XmlObjectConfig;
import ru.nsu.fit.g20221.DIContainer.util.JaxbUtil;

public class ConfigurationReaderImpl implements ConfigurationReader {
    @Override
    public Collection<XmlObjectConfig> readConfigurationFromStream(InputStream stream) throws Exception {
        XMLConfigure xmlConfigure = JaxbUtil.unmarshall(XMLConfigure.class, stream);

        return xmlConfigure.getObjectConfig();
    }

    @Override
    public Collection<JavaObjectConfig> readConfigurationFromClass(String className) {
        //TODO добавить чтение конфигурации из джава класса(ayya)
        return null;
    }

    @Override
    public Collection<ScanObjectConfig> readConfigurationFromComponentScan() {
        Reflections reflections = new Reflections("", new TypeAnnotationsScanner(), new SubTypesScanner());
        Set<Class<?>> scannedClasses = reflections.getTypesAnnotatedWith(Component.class);
        List<ScanObjectConfig> scanObjectConfigs = new ArrayList<>();
        for (Class scannedClass : scannedClasses) {
            Constructor[] constructors = scannedClass.getConstructors();
            if (constructors.length > 1) {
                throw new RuntimeException("More then 1 constructor");
            }
            Component annotation = (Component) scannedClass.getAnnotation(Component.class);
            scanObjectConfigs.add(new ScanObjectConfig(annotation.name(), constructors[0]));
        }
        return scanObjectConfigs;
    }
}
