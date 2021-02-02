package htwb.ai;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;

public class CheckClassMethods {
    public Method[] methodsWithAnnotation;
    public Class userClass;
    public String className;

    public int run(String[] arguments) throws NullPointerException {
        if (arguments.length > 0) {
            className = arguments[0];
        } else {
            System.err.println("Error: No class specified.");
            System.out.println("Usage: java -jar runmerunner-WIMI.jar classToExecute");
            throw new NullPointerException();
        }

        try {
            userClass = Class.forName(className);
            methodsWithAnnotation = userClass.getDeclaredMethods();
            ArrayList<Method> runMeMethods = new ArrayList();
            ArrayList<Method> notInvocableMethods = new ArrayList<>();

            System.out.println("Analyzed class '" + className + "':");
            System.out.println("Methods without @RunMe: ");
            for (Method m : methodsWithAnnotation) {
                if (m.getAnnotation(RunMe.class) == null) {
                    System.out.println(m.getName());
                } else {
                    runMeMethods.add(m);
                }
            }
            System.out.println("Methods with @RunMe: ");
            //TODO: not invocable methods herausfiltern und danach ausgeben.
            for (Method m : runMeMethods) {
                if (Modifier.isPublic(m.getModifiers())) {
                    System.out.println(m.getName());
                } else {
                    notInvocableMethods.add(m);
                }
            }
            System.out.println("not invokeable: ");
            for (Method m : notInvocableMethods) {
                System.out.println(m.getName() + ": IllegalAccessException");
            }

        } catch (ClassNotFoundException e) {
            System.err.println("Error: Could not find class " + className);
            System.out.println("Usage: java -jar runmerunner-WIMI.jar classToExecute");
            return 1;
        }
        return 0;
    }
}
