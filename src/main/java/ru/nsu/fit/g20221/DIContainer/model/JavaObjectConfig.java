package ru.nsu.fit.g20221.DIContainer.model;

import ru.nsu.fit.g20221.DIContainer.annotation.Name;

import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class JavaObjectConfig implements AutoObjectConfig {
    private final Object configurationObject;
    private final String name;
    private final List<Class> dependenciesByClass;
    private final List<String> dependenciesByName;
    private final Method creationMethod;

    public JavaObjectConfig(Object configurationObject, Method creationMethod) {
        this.configurationObject = configurationObject;
        this.creationMethod = creationMethod;
        this.name = creationMethod.getName();
        this.dependenciesByClass = new ArrayList<>();
        this.dependenciesByName = new ArrayList<>();
        Class[] types = creationMethod.getParameterTypes();
        Annotation[][] annotations = creationMethod.getParameterAnnotations();
        for (int i = 0; i < types.length; i++) {
            String argName = checkAnnotated(annotations[i]);
            if (argName != null) {
                dependenciesByName.add(argName);
            } else {
                dependenciesByClass.add(types[i]);
            }
        }
    }

    @Nullable
    private String checkAnnotated(Annotation[] annotations) {
        for (Annotation annotation : annotations) {
            if (annotation.annotationType().equals(Name.class)) {
                Name paramName = (Name) annotation;
                return paramName.value();
            }
        }
        return null;
    }


    public List<Class> getDependenciesByClass() {
        return dependenciesByClass;
    }

    public List<String> getDependenciesByName() {
        return dependenciesByName;
    }

    public String getName() {
        return name;
    }

    public ObjectMeta createObject(
            Map<Class, Object> dependenciesByClass,
            Map<String, Object> dependenciesByName
    ) {
        List<Object> objects = new ArrayList<>();
        Class[] types = creationMethod.getParameterTypes();
        Annotation[][] annotations = creationMethod.getParameterAnnotations();
        for (int i = 0; i < types.length; i++) {
            String argName = checkAnnotated(annotations[i]);
            if (argName != null) {
                objects.add(dependenciesByName.get(argName));
            } else {
                objects.add(dependenciesByClass.get(types[i]));
            }
        }
        try {
            Object instance = creationMethod.invoke(configurationObject, objects.toArray());
            return new ObjectMeta(Scope.SINGLETON, () -> instance);
        } catch (Exception e) {
            throw new RuntimeException("Can't instance object");
        }
    }

    public Method getCreationMethod() {
        return creationMethod;
    }
}
