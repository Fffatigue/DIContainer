package ru.nsu.fit.g20221.DIContainer.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.List;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "constructorArgs")
public class ConstructorArgs {
    @XmlElement
    private List<Property> property;

    public ConstructorArgs() {
    }

    public ConstructorArgs(List<Property> property) {
        this.property = property;
    }

    public List<Property> getProperty() {
        return property;
    }

    public void setProperty(List<Property> property) {
        this.property = property;
    }
}
