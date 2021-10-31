package computer;

public class CPU {

    //constructor
    public CPU(Memory memory) {
        this.alu = new ALU();
        this.cu = new CU();
        this.registers = new Register[ERegister.values().length];

        //레지스터 초기화
        this.registers[0] = new IR((short) 0);
        this.registers[6] = new Status((short) 0);
        for(int i=1; i<registers.length-1; i++){
            this.registers[i] = new Register((short) 0);
        }
        //메모리 associate
        this.associate(memory);
    }

    //pc, sp 세팅
    public void setPcSp(short pc, short sp){
        this.registers[ERegister.ePC.ordinal()] = new Register(pc);
        this.registers[ERegister.eSP.ordinal()] = new Register(sp);
    }

    public void run() {
        setPowerOn();
        while(isPowerOn()){
            this.fetch();
            this.excute();
        }
        System.out.println("프로그램을 종료합니다.");
    }

    //methods
    //ir까지 메모리로부터 instruction 가져옴
    private void fetch() {
        //load next instruction from memory to IR
        //PC -> MAR로 옮긴다.
        this.registers[ERegister.eMAR.ordinal()].setValue((short) ((this.registers[ERegister.ePC.ordinal()].getValue())/Props.BYTE_));//mar은 3
        this.registers[ERegister.eMBR.ordinal()].setValue(this.memory.load(this.registers[ERegister.eMAR.ordinal()].getValue()));
        this.registers[ERegister.eIR.ordinal()].setValue(this.registers[ERegister.eMBR.ordinal()].getValue());
    }


    private void excute() {
        System.out.println("-----------------");
        System.out.println(EOpCode.values()[((CPU.IR) this.registers[ERegister.eIR.ordinal()]).getOperand()] + " "+ ((IR) this.registers[ERegister.eIR.ordinal()]).getOperator());

        switch(EOpCode.values()[((CPU.IR) this.registers[ERegister.eIR.ordinal()]).getOperand()]){
            case eHALT:
                shutPowerOn();
                break;
            case eLDA:
                this.load();
                cu.upPCvalue();
                break;
            case eLDC:
                this.loadC();
                cu.upPCvalue();
                break;
            case eSTA:
                this.store();
                cu.upPCvalue();
                break;
            case eADDA:
                this.alu.add(((CPU.IR) this.registers[ERegister.eIR.ordinal()]).getOperator());
                cu.upPCvalue();
                break;
            case eADDC:
                this.alu.addC(((CPU.IR) this.registers[ERegister.eIR.ordinal()]).getOperator());
                cu.upPCvalue();
                break;
            case eSUBA:
                this.alu.sub(((CPU.IR) this.registers[ERegister.eIR.ordinal()]).getOperator());
                cu.upPCvalue();
                break;
            case eSUBC:
                this.alu.subC(((CPU.IR) this.registers[ERegister.eIR.ordinal()]).getOperator());
                cu.upPCvalue();
                break;
            case eMULA:
                this.alu.mul(((CPU.IR) this.registers[ERegister.eIR.ordinal()]).getOperator());
                cu.upPCvalue();
                break;
            case eMULC:
                this.alu.mulC(((CPU.IR) this.registers[ERegister.eIR.ordinal()]).getOperator());
                cu.upPCvalue();
                break;
            case eDIVA:
                this.alu.div(((CPU.IR) this.registers[ERegister.eIR.ordinal()]).getOperator());
                cu.upPCvalue();
                break;
            case eDIVC:
                this.alu.divC(((CPU.IR) this.registers[ERegister.eIR.ordinal()]).getOperator());
                cu.upPCvalue();
                break;
            case eANDA:
                this.alu.and(((CPU.IR) this.registers[ERegister.eIR.ordinal()]).getOperator());
                cu.upPCvalue();
                break;
            case eJMP:
                this.cu.jump(((CPU.IR) this.registers[ERegister.eIR.ordinal()]).getOperator());
                break;
            case eJMPBZ:
                this.cu.jumpBZ(((CPU.IR) this.registers[ERegister.eIR.ordinal()]).getOperator());
                break;
            case eJMPEQ:
                this.cu.jumpEQ(((CPU.IR) this.registers[ERegister.eIR.ordinal()]).getOperator());
                break;
            default:
                break;
        }
        System.out.println("-----------------");
    }

    private enum EOpCode {
        eHALT, // 0
        eLDA,  // 1
        eLDC,  // 2
        eSTA,  // 3
        eADDA, // 4
        eADDC, // 5
        eSUBA, // 6
        eSUBC, // 7
        eMULA, // 8
        eMULC, // 9
        eDIVA, // A
        eDIVC, // B
        eANDA,  // C
        eJMP,  // D
        eJMPBZ,// E
        eJMPEQ,// F
    }

    private class CU{
        public void upPCvalue(){
            short prePC = registers[ERegister.ePC.ordinal()].value;
            System.out.println("PC: "+prePC+" -> "+(prePC+Props.BYTE_)+".");
            registers[ERegister.ePC.ordinal()].setValue((short) (prePC+Props.BYTE_));
        }

        public void jmpPCvalue(short operator){
            short prePC = registers[ERegister.ePC.ordinal()].getValue();
            registers[ERegister.ePC.ordinal()].setValue((operator));
            System.out.println("PC: "+prePC+" ->" +registers[ERegister.ePC.ordinal()].getValue());
        }

        public void updateSPvalue(short addr){
            short value = registers[ERegister.eSP.ordinal()].value;
            System.out.println("SP: "+value+" -> "+(short) (memory.getHeaderSize() + memory.getCodeSize() + addr));
            registers[ERegister.eSP.ordinal()].setValue((short) (memory.getHeaderSize() + memory.getCodeSize() + addr));
        }

        public void jump(short operator) {
            jmpPCvalue(operator);
        }

        public void jumpBZ(short operator) {
            if(((Status)registers[ERegister.eStatus.ordinal()]).belowZ()){
                System.out.println(operator+"로 JMP합니다.");
                jmpPCvalue(operator);
            }
            else cu.upPCvalue();
        }

        public void jumpEQ(short operator) {
            if(((Status)registers[ERegister.eStatus.ordinal()]).eqaul()){
                System.out.println(operator+"로 JMP합니다.");
                jmpPCvalue(operator);
            }
            else cu.upPCvalue();
        }
    }

    private class ALU{

        public void add(short operator) {
            short acVal = registers[ERegister.eAC.ordinal()].getValue();
            short loadedValue = memory.load((short) ((operator + memory.getDataStart())/2));
            System.out.println("ADD: 현재 AC에 저장된 "+acVal+" 와 "+loadedValue+"의 합 "+ (acVal+loadedValue) +"을 AC에 저장합니다.");
            registers[ERegister.eAC.ordinal()].setValue((short) (acVal+loadedValue));
        }

        public void addC(short operator) {
            short acVal = registers[ERegister.eAC.ordinal()].getValue();
            System.out.println("ADD: 현재 AC에 저장된 "+acVal+" 와 "+operator+"의 합 "+ (acVal+operator) +"을 AC에 저장합니다.");
            registers[ERegister.eAC.ordinal()].setValue((short) (acVal+operator));
        }

        public void sub(short operator) {
            short acVal = registers[ERegister.eAC.ordinal()].getValue();
            short loadedValue = memory.load((short) ((operator + memory.getDataStart())/2));
            System.out.println("ADD: 현재 AC에 저장된 "+acVal+" 와 "+loadedValue+"의 차 "+ (acVal-loadedValue) +"을 AC에 저장합니다.");
            registers[ERegister.eAC.ordinal()].setValue((short) (acVal-loadedValue));
        }

        public void subC(short operator) {
            short acVal = registers[ERegister.eAC.ordinal()].getValue();
            System.out.println("ADD: 현재 AC에 저장된 "+acVal+" 와 "+operator+"의 차 "+ (acVal-operator) +"을 AC에 저장합니다.");
            registers[ERegister.eAC.ordinal()].setValue((short) (acVal-operator));
        }

        public void mul(short operator) {
            short acVal = registers[ERegister.eAC.ordinal()].getValue();
            short loadedValue = memory.load((short) ((operator + memory.getDataStart())/2));
            System.out.println("ADD: 현재 AC에 저장된 "+acVal+" 와 "+loadedValue+"의 곱 "+ (acVal*loadedValue) +"을 AC에 저장합니다.");
            registers[ERegister.eAC.ordinal()].setValue((short) (acVal*loadedValue));
        }

        public void mulC(short operator) {
            short acVal = registers[ERegister.eAC.ordinal()].getValue();
            System.out.println("ADD: 현재 AC에 저장된 "+acVal+" 와 "+operator+"의 곱 "+ (acVal*operator) +"을 AC에 저장합니다.");
            registers[ERegister.eAC.ordinal()].setValue((short) (acVal*operator));
        }

        public void div(short operator) {
            short acVal = registers[ERegister.eAC.ordinal()].getValue();
            short loadedValue = memory.load((short) ((operator + memory.getDataStart())/2));
            System.out.println("ADD: 현재 AC에 저장된 "+acVal+" 와 "+loadedValue+"의 나눗셈 결과 "+ (acVal/loadedValue) +"을 AC에 저장합니다.");
            registers[ERegister.eAC.ordinal()].setValue((short) (acVal/loadedValue));
        }

        public void divC(short operator) {
            short acVal = registers[ERegister.eAC.ordinal()].getValue();
            System.out.println("ADD: 현재 AC에 저장된 "+acVal+" 와 "+operator+"의 나눗셈 결과 "+ (acVal/operator) +"을 AC에 저장합니다.");
            registers[ERegister.eAC.ordinal()].setValue((short) (acVal/operator));
        }

        public void and(short operator) {
            short acVal = registers[ERegister.eAC.ordinal()].getValue();
            short loadedValue = memory.load((short) ((operator + memory.getDataStart())/2));
            registers[ERegister.eAC.ordinal()].setValue((short) (acVal&loadedValue));
        }
    }


    private class Register{
        public Register(short value) {
            this.value = value;
        }
        protected short value;
        public short getValue() {return this.value;}
        public void setValue(short value) {this.value = value;}

    }

    public class Status extends Register{

        private boolean[] status = new boolean[4];
        public Status(short value) {
            super(value);
        }

        public boolean belowZ(){
            for (int i = 0; i < 4; i++) status[i] = false;
            short value = registers[ERegister.eAC.ordinal()].getValue();
            if(value <= 0) status[1] = true;
            else status[1] = false;
            return status[1];
        }

        public boolean eqaul(){
            for (int i = 0; i < 4; i++) status[i] = false;
            short value = registers[ERegister.eAC.ordinal()].getValue();
            if(value == 0) status[0] = true;
            else status[0] = false;
            return status[0];
        }
    }

    public class IR extends Register{
        public IR(short value) {
            super(value);
        }
        public short getOperator() {
            return (short)(this.value & 0x00ff);
        }
        public short getOperand() {
            return (short)((this.value & 0xff00)>>8);
        }
    }

    //component
    private ALU alu;
    private CU cu;
    Register registers[];
    //association 연결.
    private Memory memory;

    //state
    private boolean bPowerOn;
    private boolean isPowerOn() {
        return this.bPowerOn;
    }
    public void setPowerOn() {
        this.bPowerOn = true;
    }

    public void shutPowerOn() {
        this.bPowerOn = false;
    }

    private enum ERegister{
        eIR,
        eSP,
        ePC,
        eAC,
        eMBR,
        eMAR,
        eStatus
    }

    //associate
    public void associate(Memory memory) {
        this.memory = memory;
    }

    private void load() {
        //IR의 operator에 있는 값을 메모리한테 load시킨다.
        System.out.println();
        short loadedValue = this.memory.load((short) ((((IR) this.registers[ERegister.eIR.ordinal()]).getOperator()+ memory.getDataStart())/2));
        //그러면 메모리가 해당 주소에 있는 값을 넘겨준다.
        System.out.println("AC에 load된 값은 "+loadedValue+"입니다.");
        //받은 값을 AC에 세팅한다.
        this.registers[ERegister.eAC.ordinal()].setValue(loadedValue);
    }

    private void loadC() {
        //IR의 operator에 있는 값을 메모리한테 load시킨다.
        System.out.println();
        short loadedValue = ((IR) this.registers[ERegister.eIR.ordinal()]).getOperator();
        //그러면 메모리가 해당 주소에 있는 값을 넘겨준다.
        System.out.println("AC에 load된 값은 "+loadedValue+"입니다.");
        //받은 값을 AC에 세팅한다.
        this.registers[ERegister.eAC.ordinal()].setValue(loadedValue);
    }

    private void store(){
        //sp를 IR의 operator를 뽑아서 update한다.
        short irOperator = ((IR) this.registers[ERegister.eIR.ordinal()]).getOperator();
        cu.updateSPvalue((irOperator));
        //sp에 있는 값을 MAR로 옮긴다.
        this.registers[ERegister.eMAR.ordinal()].setValue((short) ((this.registers[ERegister.eSP.ordinal()].getValue())/2));
        //AC에 있는 값을 뽑아서 MBR에 옮긴다
        this.registers[ERegister.eMBR.ordinal()].setValue(this.registers[ERegister.eAC.ordinal()].getValue());
        //메모리에게 mar과 mbr을 주면서 mar에 mbr을 저장하라고 명령한다.
        this.memory.store(this.registers[ERegister.eMAR.ordinal()].getValue(), this.registers[ERegister.eMBR.ordinal()].getValue());
    }
}
