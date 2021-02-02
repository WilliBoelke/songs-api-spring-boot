package htwb.ai;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.*;

class MainClassTest {
    private CheckClassMethods ccmTest;

    @BeforeEach
    public void setUp() {
        ccmTest = new CheckClassMethods();
    }

    @Test
    public void testTest() throws ClassNotFoundException {
        assertThrows(NullPointerException.class, () -> ccmTest.run(null));
    }


    @Test
    public void testBad_unknownClass() throws ClassNotFoundException {
        String[] mockArgs = new String[1];
        mockArgs[0] = "blub";
//        assertThrows(ClassNotFoundException.class, () -> ccmTest.run(mockArgs));
        assertEquals(1, ccmTest.run(mockArgs));
    }

    @Test
    public void testGut_ClassFound() throws ClassNotFoundException {
        String[] mockArgs = new String[1];
        mockArgs[0] = ClassWithAnnotations.class.getName();
        ccmTest.run(mockArgs);
        Method[] result = ccmTest.methodsWithAnnotation;
        assertEquals(9, result.length);
    }

    @Test
    public void testGut_ClassFound2() throws InvocationTargetException, IllegalAccessException {
        ClassWithAnnotations cwA = new ClassWithAnnotations();
        String[] mockArgs = new String[1];
        mockArgs[0] = ClassWithAnnotations.class.getName();
        ccmTest.run(mockArgs);
        Method[] result = ccmTest.methodsWithAnnotation;
        System.out.println("Output: ");
        for (Method m : result) {
            if (Modifier.isPublic(m.getModifiers())) {
                assertEquals(1, m.invoke(cwA));
            } else if (Modifier.isProtected(m.getModifiers())) {
                assertEquals(3, m.invoke(cwA));
            }
        }
    }
}