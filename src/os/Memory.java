package os;

import java.util.LinkedList;
import java.util.Queue;

public class Memory {

    private short[] process;

    public void loadProcess(short[] process){
        this.process = process;
    }
    public short getHeaderSize(){
        return process[Props.HEADER_SIZE_IDX];
    }

    public short getCodeSize(){
        return process[Props.CODE_SIZE_IDX];
    }

    public short getDataStart(){
        return (short) (getHeaderSize()+getCodeSize());
    }

    public short load(short mar) {
        return process[mar];
    }

    public void store(short mar, short mbr) {
        process[mar]  = mbr;
//        System.out.println("process[" + mar + "]에  현재 AC의 값 " + mbr + "을 할당합니다.");
    }

    public static class ReadyQueue{
        public static Queue<Process> rq;
        public ReadyQueue() {
            rq = new LinkedList<>();
        }
        public static void enqueue(Process process) {
            rq.add(process);
        }
        public static void dequeue(Process process){
            rq.poll();
        }
    }
}
