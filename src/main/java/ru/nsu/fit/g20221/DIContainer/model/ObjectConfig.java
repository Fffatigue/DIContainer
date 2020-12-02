package ru.nsu.fit.g20221.DIContainer.model;

import javax.xml.bind.annotation.*;
import java.util.List;

/**
 * Object configuration. Used for creation.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "objectConfig")
public class ObjectConfig {
    /**
     * Scope of object.
     */
    @XmlAttribute(required = true)
    private Scope scope;
    /**
     * Object's name.
     */
    @XmlAttribute(required = true)
    private String name;
    /**
     * Class name of object.
     */
    @XmlAttribute(required = true)
    private String className;
    /**
     * Ordered constructor args names.
     */
    @XmlElement(required = true)
    private ConstructorArgs constructorArgs;

    public ObjectConfig() {
    }

    public ObjectConfig(Scope scope, String name, String className, ConstructorArgs constructorArgs) {
        this.scope = scope;
        this.name = name;
        this.className = className;
        this.constructorArgs = constructorArgs;
    }

    public Scope getScope() {
        return scope;
    }

    public String getName() {
        return name;
    }

    public String getClassName() {
        return className;
    }

    public ConstructorArgs getConstructorArgs() {
        return constructorArgs;
    }

    public void setScope(Scope scope) {
        this.scope = scope;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public void setConstructorArgs(ConstructorArgs constructorArgs) {
        this.constructorArgs = constructorArgs;
    }
}
