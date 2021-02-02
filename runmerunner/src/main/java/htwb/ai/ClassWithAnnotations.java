package htwb.ai;

public class ClassWithAnnotations {
    public int noRunMeOne() {
        System.out.println("No @RunMe");
        return 1;
    }

    public int noRunMeTwo() {
        System.out.println("No @RunMe");
        return 1;
    }

    @RunMe
    public int runMeMethodOne() {
        System.out.println("RunMeOne");
        return 1;
    }

    @RunMe
    public int runMeMethodTwo() {
        System.out.println("RunMeTwo");
        return 1;
    }

    @RunMe
    public int runMeMethodThree() {
        System.out.println("RunMeThree");
        return 1;
    }

    @RunMe
    public int runMeMethodFour() {
        System.out.println("RunMeFour");
        return 1;
    }

    @RunMe
    public int runMeMethodFive() {
        System.out.println("RunMeFive");
        return 1;
    }

    @RunMe
    private int runMeMethodSix_NotInvokeable(int number) {
        System.out.println("RunMe" + number + " is not invocable.");
        return 2;
    }

    @RunMe
    protected int runMeMethodSeven_Protected() {
        System.out.println("RunMeSeven Protected");
        return 3;
    }

}
