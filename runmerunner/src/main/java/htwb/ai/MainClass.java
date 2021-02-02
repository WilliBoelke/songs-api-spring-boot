package htwb.ai;

import java.lang.reflect.Method;
import java.util.ArrayList;

public class MainClass {
    public static void main(String[] arguments) throws ClassNotFoundException {
        CheckClassMethods ccm = new CheckClassMethods();
        ccm.run(arguments);
    }

}
