package ru.nsu.fit.g20221.DIContainer.model;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

public class JavaObjectConfig extends AutoObjectConfig {
    private final Object configurationObject;
    private final Method creationMethod;

    public JavaObjectConfig(Object configurationObject, Method creationMethod) {
        super(creationMethod.getName(), creationMethod.getParameterTypes(), creationMethod.getParameterAnnotations());
        this.configurationObject = configurationObject;
        this.creationMethod = creationMethod;
    }

    public ObjectMeta createObject(
            Map<Class<?>, Object> dependenciesByClass,
            Map<String, Object> dependenciesByName
    ) {
        List<Object> objects = computeObjectParams(dependenciesByClass, dependenciesByName,
                creationMethod.getParameterTypes(), creationMethod.getParameterAnnotations());
        try {
            Object instance = creationMethod.invoke(configurationObject, objects.toArray());
            return new ObjectMeta(Scope.SINGLETON, () -> instance);
        } catch (Exception e) {
            throw new RuntimeException("Can't instance object");
        }
    }

    public Class<?> getCreatedClass() {
        return creationMethod.getDeclaringClass();
    }

}
