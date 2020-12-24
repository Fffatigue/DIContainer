package ru.nsu.fit.g20221.DIContainer.model;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Map;

public class ScanObjectConfig extends AutoObjectConfig {
    private final Constructor<?> constructor;

    public ScanObjectConfig(String name, Constructor<?> constructor) {
        super(name, constructor.getParameterTypes(), constructor.getParameterAnnotations());
        this.constructor = constructor;
    }

    @Override
    public Class<?> getCreatedClass() {
        return constructor.getDeclaringClass();
    }


    public ObjectMeta createObject(Map<Class<?>, Object> dependenciesByClass,
                                   Map<String, Object> dependenciesByName) {
        List<Object> objects = computeObjectParams(dependenciesByClass, dependenciesByName,
                constructor.getParameterTypes(), constructor.getParameterAnnotations());
        try {
            Object instance = constructor.newInstance(objects.toArray());
            return new ObjectMeta(Scope.SINGLETON, () -> instance);
        } catch (Exception e) {
            throw new RuntimeException("Can't instance object");
        }
    }
}
