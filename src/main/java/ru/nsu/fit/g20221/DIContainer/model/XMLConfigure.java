package ru.nsu.fit.g20221.DIContainer.model;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "xmlConfigure")
@XmlRootElement(name = "configure")
public class XMLConfigure {
    @XmlElement(required = true)
    private List<XmlObjectConfig> objectConfig;

    public XMLConfigure() {
    }

    public XMLConfigure(List<XmlObjectConfig> objectConfig) {
        this.objectConfig = objectConfig;
    }

    public List<XmlObjectConfig> getObjectConfig() {
        return objectConfig;
    }

    public void setObjectConfig(List<XmlObjectConfig> objectConfig) {
        this.objectConfig = objectConfig;
    }
}
