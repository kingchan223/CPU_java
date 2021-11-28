package os;

public class Process {

    public short[] process;

    int pcbSize;
    int dataSize;
    int codeSize;
    int stackSize;
    int HeapSize;

    public Process(int dataSize, int codeSize, int pcbSize, int stackSize, int heapSize) {
        this.dataSize = dataSize;
        this.codeSize = codeSize;
        this.pcbSize = pcbSize;
        this.stackSize = stackSize;
        HeapSize = heapSize;
    }


    private static class PCB{
        EState estate;
        int id;
        int CD, DS, SS, HS;
        int PC, SP;
        CPU.Register[] registers;
    }
    public enum EState{
        eReady, eRunning, eWaiting,
    }


}
