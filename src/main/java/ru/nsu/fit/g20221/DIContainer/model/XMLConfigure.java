package ru.nsu.fit.g20221.DIContainer.model;

import javax.xml.bind.annotation.*;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "xmlConfigure")
@XmlRootElement(name = "configure")
public class XMLConfigure {
    @XmlElement(required = true)
    private List<ObjectConfig> objectConfig;

    public XMLConfigure() {
    }

    public XMLConfigure(List<ObjectConfig> objectConfig) {
        this.objectConfig = objectConfig;
    }

    public List<ObjectConfig> getObjectConfig() {
        return objectConfig;
    }

    public void setObjectConfig(List<ObjectConfig> objectConfig) {
        this.objectConfig = objectConfig;
    }
}
