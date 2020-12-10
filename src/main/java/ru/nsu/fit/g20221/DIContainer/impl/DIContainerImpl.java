package ru.nsu.fit.g20221.DIContainer.impl;

import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
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

    private final Map<String, ObjectMeta> registredObjects;
    private final Map<String, ObjectConfig> existedObjects;
    private final ConfigurationReader configurationReader;

    public DIContainerImpl(ConfigurationReader configurationReader) {
        this.registredObjects = new HashMap<>();
        this.existedObjects = new HashMap<>();
        this.configurationReader = configurationReader;
    }

    public DIContainerImpl(Map<String, ObjectMeta> registredObjects,
                           Map<String, ObjectConfig> existedObjects,
                           ConfigurationReader configurationReader) {
        this.registredObjects = registredObjects;
        this.existedObjects = existedObjects;
        this.configurationReader = configurationReader;
    }

    @Override
    public void registerObject(Object object, String name) {
        registredObjects.put(name, new ObjectMeta(Scope.SINGLETON, () -> object));
    }

    @Override
    public void loadConfig(InputStream config) throws Exception {
        Collection<ObjectConfig> objectConfigs = configurationReader.readConfigurationFromStream(config);
        int objectsToCreate;

        for (ObjectConfig objectConfig : objectConfigs) {
            if (existedObjects.containsKey(objectConfig.getName())) {
                log.error("Object name " + objectConfig.getName() + " duplicate");
                throw new RuntimeException("Object name " + objectConfig.getName() + " duplicate");
            }
            existedObjects.put(objectConfig.getName(), objectConfig);
        }

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
        return registredObjects.keySet();
    }

    @Override
    public Optional<Object> getObject(String name) {
        ObjectMeta objectMeta = registredObjects.get(name);
        if (objectMeta == null) {
            return Optional.empty();
        } else {
            return Optional.of(objectMeta.getObject());
        }
    }

    @Override
    public void unregisterObject(String name) {
        ObjectMeta objectMeta = registredObjects.remove(name);
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
        if (registredObjects.containsKey(objectConfig.getName())) {
            return true;
        }

        List<ObjectConfig> args = new ArrayList<>();
        if (objectConfig.getConstructorArgs() != null) {
            List<String> constructorArgsName = objectConfig.getConstructorArgs()
                    .getProperty()
                    .stream()
                    .map(Property::getRef)
                    .collect(Collectors.toList());

            for (String argName : constructorArgsName) {
                ObjectConfig arg = existedObjects.get(argName);
                if (arg == null) {
                    log.error("Can't create object " + objectConfig.getName() + " because arg " + argName + " doesn't exist.");
                    throw new RuntimeException("Can't create object " + objectConfig.getName() + " because arg " + argName + " doesn't exist.");
                }
                args.add(arg);
            }
        }
        ObjectMeta objectMeta = new ObjectMeta(objectConfig.getScope(),
                createObject(args, objectConfig.getScope(), objectConfig.getClassName()));
        registredObjects.put(objectConfig.getName(), objectMeta);
        return true;
    }

    private Supplier<Object> createObject(
            List<ObjectConfig> constructorArgs,
            Scope objectScope,
            String className
    ) {
        Class objectClass = null;
        try {
            objectClass = Class.forName(className);
        } catch (ClassNotFoundException e) {
            log.error("Class name" + className + " doesn't exist");
            throw new RuntimeException(e);
        }
        Class[] argsClasses = constructorArgs
                .stream()
                .map(it -> {
                    try {
                        return Class.forName(it.getClassName());
                    } catch (ClassNotFoundException e) {
                        log.error("Class " + className + " doesn't have required constructor");
                        throw new RuntimeException("Class " + className + " doesn't have required constructor");
                    }
                }).toArray(Class[]::new);
        try {
            Constructor constructors = objectClass
                    .getDeclaredConstructor(argsClasses);
            constructorArgs
                    .stream()
                    .forEach(it -> {
                        if (!registredObjects.containsKey(it.getName())) {
                            if (!tryToCreate(it)) {
                                log.error("Can't create object of class " + className + " because can't create parameter " + it.getName());
                                throw new RuntimeException("Can't create object of class " + className + " because can't create parameter " + it.getName());
                            }
                        }
                    });

            Object[] constructorArgsSupplier = constructorArgs
                    .stream()
                    .map(it -> registredObjects.get(it.getName()).getObject())
                    .toArray(Object[]::new);
            if (objectScope == Scope.SINGLETON) {
                try {
                    Object o = constructors.newInstance(constructorArgsSupplier);
                    postConstruct(o);
                    return () -> o;
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                    log.error("Can't create object of class " + className, e);
                    throw new RuntimeException(e);
                }
            } else {
                return () -> {
                    try {
                        Object a = constructors.newInstance(constructorArgsSupplier);
                        postConstruct(a);
                        return a;
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                        log.error("Can't create object of class " + className, e);
                        throw new RuntimeException(e);
                    }
                };
            }

        } catch (NoSuchMethodException e) {
            log.error("Can't create object of class " + className, e);
            throw new RuntimeException(e);
        }
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
