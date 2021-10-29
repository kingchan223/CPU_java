package computer;

public class Memory {

    private short process[];

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

    /*   메모리가 CPU의 MAR, MBR을 알고 있어야 한다. */
    public short load(short mar) {
        return process[mar];
    }

    public void store(short mar, short mbr) {
        process[mar]  = mbr;
        System.out.println("process[" + mar + "]에  현재 AC의 값 " + mbr + "을 할당합니다.");
    }
}
