package ru.nsu.fit.g20221.DIContainer.impl;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import com.google.common.collect.ArrayListMultimap;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.nsu.fit.g20221.DIContainer.DIContainer;
import ru.nsu.fit.g20221.DIContainer.impl.testModel.ConfigurationObject;
import ru.nsu.fit.g20221.DIContainer.impl.testModel.House;
import ru.nsu.fit.g20221.DIContainer.impl.testModel.Human;
import ru.nsu.fit.g20221.DIContainer.impl.testModel.Pet;
import ru.nsu.fit.g20221.DIContainer.impl.testModel.ScannedObject;
import ru.nsu.fit.g20221.DIContainer.model.ObjectMeta;
import ru.nsu.fit.g20221.DIContainer.model.Scope;

/**
 * Tests for {@link DIContainerImpl}
 */
public class DIContainerImplTest {

    @Test
    void testLoadConfig() {
        DIContainer diContainer = new DIContainerImpl(new ConfigurationReaderImpl());
        Assertions.assertDoesNotThrow(
                () -> diContainer.loadXmlConfig(DIContainerImplTest.class.getClassLoader().getResourceAsStream(
                        "xmlConfigure.xml"))
        );
        Human human1 = (Human) diContainer.getObject("human").get();
        Human human2 = (Human) diContainer.getObject("human").get();
        Assertions.assertEquals(human1, human2);

        House house1 = (House) diContainer.getObject("house").get();
        House house2 = (House) diContainer.getObject("house").get();
        Assertions.assertEquals(house1, house2);
        Assertions.assertEquals(house1.getHuman(), house2.getHuman());

        Pet pet1 = (Pet) diContainer.getObject("pet").get();
        Pet pet2 = (Pet) diContainer.getObject("pet").get();
        Assertions.assertEquals("Pet1", pet1.getName());
        Assertions.assertEquals("Pet2", pet2.getName());
        Assertions.assertNotEquals(pet1, pet2);
        Assertions.assertEquals(pet1.getHouse(), pet2.getHouse());
        Assertions.assertEquals(pet1.getHuman(), pet2.getHuman());

    }

    @Test
    void testJavaConfiguration() {
        DIContainer diContainer = new DIContainerImpl(new ConfigurationReaderImpl());
        Human human = new Human("human");
        House house = new House(human, "houseJavaConfig");
        diContainer.registerObject(human, "human");
        diContainer.registerObject(house, "houseJavaConfig");
        diContainer.loadJavaConfig(ConfigurationObject.class);
        Pet pet = (Pet) diContainer.getObject("pet").get();
        Assertions.assertEquals(house, pet.getHouse());
        Assertions.assertEquals(human, pet.getHuman());
    }


    @Test
    void testJavaConfigurationFailed() {
        DIContainer diContainer = new DIContainerImpl(new ConfigurationReaderImpl());
        Human human1 = new Human("human1");
        Human human2 = new Human("human2");
        House house = new House(human1, "houseJavaConfig");
        diContainer.registerObject(human1, "human1");
        diContainer.registerObject(human2, "human2");
        diContainer.registerObject(house, "houseJavaConfig");
        Assertions.assertThrows(RuntimeException.class, () -> diContainer.loadJavaConfig(ConfigurationObject.class));
    }

    @Test
    void testScan() {
        DIContainer diContainer = new DIContainerImpl(new ConfigurationReaderImpl());
        Human human = new Human("human");
        House house = new House(human, "addr");
        diContainer.registerObject(human, "human");
        diContainer.registerObject(house, "house1");
        diContainer.componentScan();
        ScannedObject scannedObject = (ScannedObject) diContainer.getObject("scanned").get();
        Assertions.assertEquals(human, scannedObject.getHuman());
        Assertions.assertEquals(house, scannedObject.getHouse());
    }

    @Test
    void testPostConstruct() {
        DIContainerImpl diContainer = new DIContainerImpl(null);
        TestAnnotationsClass testAnnotations = new TestAnnotationsClass();
        diContainer.invokeAnnotation(testAnnotations, PostConstruct.class);
        Assertions.assertEquals(0, testAnnotations.preDestroyCalls);
        Assertions.assertEquals(0, testAnnotations.withoutAnnotationCalls);
        Assertions.assertEquals(1, testAnnotations.postConstuctCalls);
    }

    @Test
    void testUnregister() {
        Map<String, ObjectMeta> objectMetaMap = new HashMap<>();
        TestAnnotationsClass testAnnotations1 = new TestAnnotationsClass();
        TestAnnotationsClass testAnnotations2 = new TestAnnotationsClass();
        objectMetaMap.put("test1", new ObjectMeta(Scope.SINGLETON, () -> testAnnotations1));
        objectMetaMap.put("test2", new ObjectMeta(Scope.PROTOTYPE, () -> testAnnotations2));

        DIContainer diContainer = new DIContainerImpl(objectMetaMap, null, ArrayListMultimap.create());
        diContainer.unregisterObject("test1");
        diContainer.unregisterObject("test2");

        Assertions.assertTrue(diContainer.getObject("test1").isEmpty());
        Assertions.assertTrue(diContainer.getObject("test2").isEmpty());

        Assertions.assertEquals(1, testAnnotations1.preDestroyCalls);
        Assertions.assertEquals(0, testAnnotations1.withoutAnnotationCalls);
        Assertions.assertEquals(0, testAnnotations1.postConstuctCalls);

        Assertions.assertEquals(0, testAnnotations2.preDestroyCalls);
        Assertions.assertEquals(0, testAnnotations2.withoutAnnotationCalls);
        Assertions.assertEquals(0, testAnnotations2.postConstuctCalls);
    }

    @Test
    void testRegister() {
        DIContainer diContainer = new DIContainerImpl(null);
        diContainer.registerObject("stringObject", "test");
        Assertions.assertEquals("stringObject", diContainer.getObject("test").get());
    }

    private static class TestAnnotationsClass {
        private int postConstuctCalls = 0;
        private int preDestroyCalls = 0;
        private int withoutAnnotationCalls = 0;

        public void test1() {
            withoutAnnotationCalls++;
        }

        @PostConstruct
        public void test2() {
            postConstuctCalls++;
        }

        @PreDestroy
        public void test6() {
            preDestroyCalls++;
        }
    }
}
