package os;

public class Storage {

    public String open(String filename){
        System.out.println("    │     storage의 open()      │");
        System.out.println("    │return address of filename│");
        System.out.println("\n");
        return filename + ".address";
    }
}
