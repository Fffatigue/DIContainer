package ru.nsu.fit.g20221.DIContainer.impl;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.nsu.fit.g20221.DIContainer.DIContainer;
import ru.nsu.fit.g20221.DIContainer.model.ObjectMeta;
import ru.nsu.fit.g20221.DIContainer.model.Scope;

/**
 * Tests for {@link DIContainerImpl}
 */
public class DIContainerImplTest {

    @Test
    void testPostConstruct() {
        DIContainerImpl diContainer = new DIContainerImpl(new HashMap<>(), null);
        TestAnnotationsClass testAnnotations = new TestAnnotationsClass();
        diContainer.postConstruct(testAnnotations);
        Assertions.assertEquals(0, testAnnotations.preDestroyCalls);
        Assertions.assertEquals(0, testAnnotations.withoutAnnotationCalls);
        Assertions.assertEquals(2, testAnnotations.postConstuctCalls);
    }

    @Test
    void testUnregister() {
        Map<String, ObjectMeta> objectMetaMap = new HashMap<>();
        TestAnnotationsClass testAnnotations1 = new TestAnnotationsClass();
        TestAnnotationsClass testAnnotations2 = new TestAnnotationsClass();
        objectMetaMap.put("test1", new ObjectMeta(Scope.SINGLETON, () -> testAnnotations1));
        objectMetaMap.put("test2", new ObjectMeta(Scope.PROTOTYPE, () -> testAnnotations2));

        DIContainer diContainer = new DIContainerImpl(objectMetaMap, null);
        diContainer.unregisterObject("test1");
        diContainer.unregisterObject("test2");

        Assertions.assertTrue(diContainer.getObject("test1").isEmpty());
        Assertions.assertTrue(diContainer.getObject("test2").isEmpty());

        Assertions.assertEquals(2, testAnnotations1.preDestroyCalls);
        Assertions.assertEquals(0, testAnnotations1.withoutAnnotationCalls);
        Assertions.assertEquals(0, testAnnotations1.postConstuctCalls);

        Assertions.assertEquals(0, testAnnotations2.preDestroyCalls);
        Assertions.assertEquals(0, testAnnotations2.withoutAnnotationCalls);
        Assertions.assertEquals(0, testAnnotations2.postConstuctCalls);
    }

    @Test
    void testRegister() {
        DIContainer diContainer = new DIContainerImpl(new HashMap<>(), null);
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

        @PostConstruct
        public int test3() {
            return postConstuctCalls++;
        }

        @PostConstruct
        public void test4(Object arg1) {
            postConstuctCalls++;
        }

        @PostConstruct
        public void test5(Object arg1, Object arg2) {
            postConstuctCalls++;
        }

        @PreDestroy
        public void test6() {
            preDestroyCalls++;
        }

        @PreDestroy
        public int test7() {
            return preDestroyCalls++;
        }

        @PreDestroy
        public void test8(Object arg1) {
            preDestroyCalls++;
        }

        @PreDestroy
        public void test9(Object arg1, Object arg2) {
            preDestroyCalls++;
        }
    }
}
