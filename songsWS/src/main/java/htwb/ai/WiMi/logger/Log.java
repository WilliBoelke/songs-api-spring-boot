package htwb.ai.WiMi.logger;

/**
 * Simple logger
 */
public  class Log
{
    public static void err(String className, String methodName, String description)
    {
        System.err.println("ERROR : " + className + " : "  + methodName + " : " + description );
    }

    public static void inf(String className, String methodName, String description)
    {
        System.out.println("INFO : " + className + " : "  + methodName + " : " + description );
    }

    public static void deb(String className, String methodName, String description)
    {
        System.out.println("DEBUG : " + className + " : "  + methodName + " : " + description );
    }
}
