package os;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Loader {//PCB를 만들기
    private short sizeHeader;
    private short sizeCodeSegment;
    private short sizeDataSegment;
    private final Memory memory;
    private final CPU cpu;
    public short[] process;

    public short getSizeHeader() {
        return sizeHeader;
    }

    public short getSizeCodeSegment() {
        return sizeCodeSegment;
    }

    public short getSizeDataSegment() {
        return sizeDataSegment;
    }

    public static void main(String[] args) {
        Loader loader = new Loader();
        loader.load(Props.FILE_PATH);
        loader.memory.loadProcess(loader.process);
        loader.cpu.setPcSp((loader.getSizeHeader()),
                (short) ((loader.getSizeHeader()+loader.getSizeCodeSegment())));
        loader.cpu.run();
        System.out.println("rank:"+loader.process[loader.process.length-1]);
    }

    public Loader() {
        this.memory = new Memory();
        this.cpu = new CPU(this.memory);
    }

    public void load(String fileName) {
        short sizeHeader = 0;
        short sizeCodeSegment = 0;
        short sizeDataSegment = 0;

        try {
            Scanner scanner = new Scanner(new File(fileName));
            if(scanner.hasNext()){
                sizeHeader = Short.decode(scanner.nextLine());
                this.sizeHeader = sizeHeader;
                sizeCodeSegment = Short.decode(scanner.nextLine());
                this.sizeCodeSegment = sizeCodeSegment;
                sizeDataSegment = Short.decode(scanner.nextLine());
                this.sizeDataSegment = sizeDataSegment;
            }
            this.process = new short[(sizeHeader+sizeCodeSegment+sizeDataSegment)/Props.BYTE_];
            process[0] = sizeHeader;
            process[1] = sizeCodeSegment;
            process[2] = sizeDataSegment;
            int i=3;
            while(scanner.hasNext()){
                process[i] = Short.decode(scanner.nextLine());
                i++;
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
