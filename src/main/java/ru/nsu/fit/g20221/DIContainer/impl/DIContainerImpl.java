package ru.nsu.fit.g20221.DIContainer.impl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import ru.nsu.fit.g20221.DIContainer.ConfigurationReader;
import ru.nsu.fit.g20221.DIContainer.DIContainer;
import ru.nsu.fit.g20221.DIContainer.model.ObjectConfig;
import ru.nsu.fit.g20221.DIContainer.model.ObjectMeta;
import ru.nsu.fit.g20221.DIContainer.model.Scope;

public class DIContainerImpl implements DIContainer {
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
    public void loadConfig(InputStream config) {
        Collection<ObjectConfig> objectConfigs = configurationReader.readConfigurationFromStream(config);
        int objectsToCreate;
        do {
            objectsToCreate = objectConfigs.size();
            if (objectsToCreate == 0) {
                return;
            }
            objectConfigs.removeIf(this::tryToRegister);

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
        if (Scope.SINGLETON.equals(objectMeta.getScope())) {
            //TODO(Sasha) object destroy
        }
    }

    @Override
    public void close() {
        getObjectNames().forEach(this::unregisterObject);
    }

    /**
     * @return {@code true} if  aregistration was successful, otherwise {@code false}
     */
    private boolean tryToRegister(ObjectConfig objectConfig) {
        List<String> constructorArgsName = objectConfig.getConstructorArgs();
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

        postConstruct(object);
    }

    private void postConstruct(Object object) {
        //TODO(Sasha) postConstruct
    }
}
