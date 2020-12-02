package ru.nsu.fit.g20221.DIContainer.impl;

import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import com.google.common.annotations.VisibleForTesting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.nsu.fit.g20221.DIContainer.ConfigurationReader;
import ru.nsu.fit.g20221.DIContainer.DIContainer;
import ru.nsu.fit.g20221.DIContainer.model.ObjectConfig;
import ru.nsu.fit.g20221.DIContainer.model.ObjectMeta;
import ru.nsu.fit.g20221.DIContainer.model.Property;
import ru.nsu.fit.g20221.DIContainer.model.Scope;

public class DIContainerImpl implements DIContainer {
    private static final Logger log = LoggerFactory.getLogger(DIContainerImpl.class);

    private final Map<String, ObjectMeta> mappedObjects;
    private final ConfigurationReader configurationReader;

    public DIContainerImpl(Map<String, ObjectMeta> mappedObjects, ConfigurationReader configurationReader) {
        this.mappedObjects = mappedObjects;
        this.configurationReader = configurationReader;
    }

    @Override
    public void registerObject(Object object, String name) {
        mappedObjects.put(name, new ObjectMeta(Scope.SINGLETON, () -> object));
    }

    @Override
    public void loadConfig(InputStream config) throws Exception {
        Collection<ObjectConfig> objectConfigs = configurationReader.readConfigurationFromStream(config);
        int objectsToCreate;
        do {
            objectsToCreate = objectConfigs.size();
            if (objectsToCreate == 0) {
                return;
            }
            objectConfigs.removeIf(this::tryToCreate);

        } while (objectsToCreate != objectConfigs.size());
    }

    @Override
    public Collection<String> getObjectNames() {
        return mappedObjects.keySet();
    }

    @Override
    public Optional<Object> getObject(String name) {
        ObjectMeta objectMeta = mappedObjects.get(name);
        if (objectMeta == null) {
            return Optional.empty();
        } else {
            return Optional.of(objectMeta.getObject());
        }
    }

    @Override
    public void unregisterObject(String name) {
        ObjectMeta objectMeta = mappedObjects.remove(name);
        if (objectMeta != null) {
            if (Scope.SINGLETON.equals(objectMeta.getScope())) {
                getMethodsAnnotatedWith(objectMeta.getObject().getClass(), PreDestroy.class).forEach(
                        m -> {
                            try {
                                m.invoke(objectMeta.getObject());
                            } catch (Exception e) {
                                log.error("Can't postConstruct object ", e);
                            }
                        });
            }
        }
    }

    @Override
    public void close() {
        getObjectNames().forEach(this::unregisterObject);
    }

    /**
     * @return {@code true} if  a registration was successful, otherwise {@code false}
     */
    private boolean tryToCreate(ObjectConfig objectConfig) {
        List<String> constructorArgsName = objectConfig.getConstructorArgs()
                .getProperty()
                .stream()
                .map(Property::getName)
                .collect(Collectors.toList());
        List<Object> args = new ArrayList<>();
        for (String argName : constructorArgsName) {
            Object arg = mappedObjects.get(argName);
            if (arg == null) {
                return false;
            }
            args.add(arg);
        }
        ObjectMeta objectMeta = new ObjectMeta(objectConfig.getScope(),
                createObject(args, objectConfig.getScope(), objectConfig.getClassName()));
        mappedObjects.put(objectConfig.getName(), objectMeta);
        return true;
    }

    private Supplier<Object> createObject(List<Object> constructorArgs, Scope objectScope, String className) {
        //TODO(Ayya) object registration

//        postConstruct(object);
        return null;
    }

    @VisibleForTesting
    void postConstruct(Object object) {
        Collection<Method> annotatedMethods = getMethodsAnnotatedWith(object.getClass(), PostConstruct.class);
        annotatedMethods.forEach(m -> {
            try {
                m.invoke(object);
            } catch (Exception e) {
                log.error("Can't postConstruct object ", e);
            }
        });
    }

    private static Collection<Method> getMethodsAnnotatedWith(Class<?> type, Class<? extends Annotation> annotation) {
        List<Method> methods = new ArrayList<>();
        Class<?> klass = type;
        while (klass != Object.class) {
            for (final Method method : klass.getDeclaredMethods()) {
                if (method.isAnnotationPresent(annotation) && method.getParameterCount() == 0) {
                    methods.add(method);
                }
            }
            klass = klass.getSuperclass();
        }
        return methods;
    }
}
