package ru.nsu.fit.g20221.DIContainer.impl;

import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.nsu.fit.g20221.DIContainer.ConfigurationReader;
import ru.nsu.fit.g20221.DIContainer.DIContainer;
import ru.nsu.fit.g20221.DIContainer.model.JavaObjectConfig;
import ru.nsu.fit.g20221.DIContainer.model.ObjectMeta;
import ru.nsu.fit.g20221.DIContainer.model.Property;
import ru.nsu.fit.g20221.DIContainer.model.Scope;
import ru.nsu.fit.g20221.DIContainer.model.XmlObjectConfig;

public class DIContainerImpl implements DIContainer {
    private static final Logger log = LoggerFactory.getLogger(DIContainerImpl.class);

    private final Map<String, ObjectMeta> registredObjectsNames;
    private final Multimap<Class, ObjectMeta> registredObjectsClasses;
    private final ConfigurationReader configurationReader;

    public DIContainerImpl(ConfigurationReader configurationReader) {
        this.registredObjectsNames = new HashMap<>();
        this.configurationReader = configurationReader;
        this.registredObjectsClasses = ArrayListMultimap.create();
    }

    public DIContainerImpl(Map<String, ObjectMeta> registredObjectsNames,
                           ConfigurationReader configurationReader,
                           Multimap<Class, ObjectMeta> registredObjectsClasses) {
        this.registredObjectsNames = registredObjectsNames;
        this.configurationReader = configurationReader;
        this.registredObjectsClasses = registredObjectsClasses;
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

    @Override
    public void registerObject(Object object, String name) {
        registredObjectsNames.put(name, new ObjectMeta(Scope.SINGLETON, () -> object));
    }

    @Override
    public void loadXmlConfig(InputStream config) throws Exception {
        Map<String, XmlObjectConfig> mappedXmlObjectConfig = new HashMap<>();
        Collection<XmlObjectConfig> objectConfigs = configurationReader.readConfigurationFromStream(config);

        for (XmlObjectConfig objectConfig : objectConfigs) {
            if (mappedXmlObjectConfig.containsKey(objectConfig.getName())) {
                log.error("Object name {} duplicate", objectConfig.getName());
                throw new RuntimeException("Object name " + objectConfig.getName() + " duplicate");
            }
            mappedXmlObjectConfig.put(objectConfig.getName(), objectConfig);
        }

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
    public void loadJavaConfig(String configClassName) {
        Map<String, JavaObjectConfig> mappedJavaObjectConfig = new HashMap<>();
        Collection<JavaObjectConfig> objectConfigs = configurationReader.readConfigurationFromClass(configClassName);
        int objectsToCreate;

        for (JavaObjectConfig objectConfig : objectConfigs) {
            if (mappedJavaObjectConfig.containsKey(objectConfig.getName())) {
                log.error("Object name {} duplicate", objectConfig.getName());
                throw new RuntimeException("Object name " + objectConfig.getName() + " duplicate");
            }
            mappedJavaObjectConfig.put(objectConfig.getName(), objectConfig);
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
        return registredObjectsNames.keySet();
    }

    @Override
    public Optional<Object> getObject(String name) {
        ObjectMeta objectMeta = registredObjectsNames.get(name);
        if (objectMeta == null) {
            return Optional.empty();
        } else {
            return Optional.of(objectMeta.getObject());
        }
    }

    @Override
    public void unregisterObject(String name) {
        ObjectMeta objectMeta = registredObjectsNames.remove(name);
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
    private boolean tryToCreate(JavaObjectConfig objectConfig) {
        //TODO тут если больше 1 подходящего аргумента нужно эксепшн кидать
        if (objectConfig.getDependenciesByClass().stream().anyMatch(c -> registredObjectsClasses.get(c).size() != 1)) {
            return false;
        }
        if (objectConfig.getDependenciesByNames().stream().anyMatch(name -> registredObjectsNames.get(name) == null)) {
            return false;
        }

        Map<Class, Object> dependenciesByClass = new HashMap<>();
        Map<String, Object> dependenciesByName = new HashMap<>();
        for(Class clazz : objectConfig.getDependenciesByClass() ) {
            dependenciesByClass.put(clazz, registredObjectsClasses.get(clazz).stream().findFirst().get());
        }
        for(String name : objectConfig.getDependenciesByNames() ) {
            dependenciesByName.put(name, registredObjectsNames.get(name));
        }
        registredObjectsNames.put(objectConfig.getName(), objectConfig.createObject(dependenciesByClass, dependenciesByName));
        return true;
    }

    /**
     * @return {@code true} if  a registration was successful, otherwise {@code false}
     */
    private boolean tryToCreate(XmlObjectConfig objectConfig) {
        List<ObjectMeta> argsMeta = new ArrayList<>();
        if (objectConfig.getConstructorArgs() != null) {
            List<String> constructorArgsName = objectConfig.getConstructorArgs()
                    .getProperty()
                    .stream()
                    .map(Property::getRef)
                    .collect(Collectors.toList());

            for (String argName : constructorArgsName) {
                ObjectMeta arg = registredObjectsNames.get(argName);
                if (arg == null) {
                    return false;
                }
                argsMeta.add(arg);
            }
        }
        List<Object> args = argsMeta.stream().map(m -> m.getObject()).collect(Collectors.toList());
        ObjectMeta objectMeta = new ObjectMeta(objectConfig.getScope(),
                createObject(args, objectConfig.getScope(), objectConfig.getClassName()));
        registredObjectsNames.put(objectConfig.getName(), objectMeta);
        try {
            Class objectClass = Class.forName(objectConfig.getClassName());
            while (objectClass != null) {
                registredObjectsClasses.put(objectClass, objectMeta);
                objectClass = objectClass.getSuperclass();
            }
        } catch (ClassNotFoundException e) {
            log.error("Class name {} doesn't exist", objectConfig.getClassName());
            throw new RuntimeException(e);
        }
        return true;
    }

    private Supplier<Object> createObject(
            List<Object> constructorArgs,
            Scope objectScope,
            String className) {
        Class objectClass;
        try {
            objectClass = Class.forName(className);
        } catch (ClassNotFoundException e) {
            log.error("Class name {} doesn't exist", className);
            throw new RuntimeException(e);
        }

        Optional<Constructor> constructor = Stream.of(objectClass.getConstructors()).filter(c ->
                c.getParameterCount() == constructorArgs.size()).filter(c -> {
                    Class[] classes = c.getParameterTypes();
                    for (int i = 0; i < classes.length || i < constructorArgs.size(); ++i) {
                        if (!classes[i].isInstance(constructorArgs.get(i))) {
                            return false;
                        }
                    }
                    return true;
                }
        ).findFirst();
        if (constructor.isEmpty()) {
            log.error("Can't find constructor for class {}", className);
            throw new RuntimeException("Can't find constructor for class " + className);
        }
        if (objectScope == Scope.SINGLETON) {
            try {
                Object o = constructor.get().newInstance(constructorArgs.toArray());
                postConstruct(o);
                return () -> o;
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                log.error("Can't create object of class " + className, e);
                throw new RuntimeException(e);
            }
        } else {
            return () -> {
                try {
                    Object a = constructor.get().newInstance(constructorArgs.toArray());
                    postConstruct(a);
                    return a;
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                    log.error("Can't create object of class " + className, e);
                    throw new RuntimeException(e);
                }
            };
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
}
