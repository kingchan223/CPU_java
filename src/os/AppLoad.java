package os;

public class AppLoad {

    public static final String filename = "example.exe";

    public static void main(String[] args) {
        Loader loader = new Loader();
        loader.load(filename);
    }
}
