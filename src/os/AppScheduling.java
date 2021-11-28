package os;

public class AppScheduling {


    public static void main(String[] args) {
        Scheduler scheduler = new Scheduler();
        scheduler.register(new Process(1,1,1,1,1));
    }
}
