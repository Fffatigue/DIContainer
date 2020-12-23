package ru.nsu.fit.g20221.DIContainer.util;

import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

public class JaxbUtil {
    public static <T> T unmarshall(
            Class<T> class_,
            InputStream is
    ) throws Exception {
        JAXBContext context = JAXBContext.newInstance(class_);
        Unmarshaller um = context.createUnmarshaller();
        JAXBElement<T> result = um.unmarshal(new StreamSource(is), class_);
        return result.getValue();
    }
}
