package ru.nsu.fit.g20221.DIContainer.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Object configuration. Used for creation.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "objectConfig")
public class XmlObjectConfig {
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

    public XmlObjectConfig() {
    }

    public XmlObjectConfig(Scope scope, String name, String className, ConstructorArgs constructorArgs) {
        this.scope = scope;
        this.name = name;
        this.className = className;
        this.constructorArgs = constructorArgs;
    }

    public Scope getScope() {
        return scope;
    }

    public void setScope(Scope scope) {
        this.scope = scope;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public ConstructorArgs getConstructorArgs() {
        return constructorArgs;
    }

    public void setConstructorArgs(ConstructorArgs constructorArgs) {
        this.constructorArgs = constructorArgs;
    }
}
