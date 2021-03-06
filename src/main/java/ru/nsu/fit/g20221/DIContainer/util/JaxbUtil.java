package ru.nsu.fit.g20221.DIContainer.util;

import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

public class JaxbUtil {
    private JaxbUtil() {
        throw new UnsupportedOperationException();
    }
    /**
     * Unmarshall an object from XML format that passed through InputStream into class.
     *
     * @param class_ class, in which an object will be unmarshalled.
     * @param is input stream with XML format.
     * @return an unmarshalled object.
     */
    public static <T> T unmarshall(
            Class<T> class_,
            InputStream is) throws RuntimeException {
        try {
            JAXBContext context = JAXBContext.newInstance(class_);
            Unmarshaller um = context.createUnmarshaller();
            JAXBElement<T> result = um.unmarshal(new StreamSource(is), class_);
            return result.getValue();
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }
}
