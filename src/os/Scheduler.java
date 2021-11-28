package os;

public class Scheduler {

    CPU cpu;

    public Scheduler() {
        Memory memory = new Memory();
        this.cpu = new CPU(memory);
    }

    public void register(String processAddress) {
        System.out.println("    │  Scheduler의 register()   │");
        System.out.println("    │      process 등록         │");
    }

    public void register(Process process) {

        System.out.println("    │  Scheduler의 register()   │");
        Memory.ReadyQueue.enqueue(process);
        CPU.CPUContext context = cpu.getContext();
        System.out.println("이전에 실행되고 있던 Process에 현재 os.CPUContext set");
        System.out.println("\n");
        Memory.ReadyQueue.dequeue(process);
        CPU.CPUContext context2 = cpu.getContext();
        cpu.setCPUContext(context2);
    }
}
