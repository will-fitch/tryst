

import java.util.LinkedList;
import java.util.List;

public class Assembly {
    
    private final List<String> instructions;
    private int labelIndex;

    public Assembly() {
        instructions = new LinkedList<>();
        labelIndex = 0;
    }

    public String getLabel(String prefix) {
        labelIndex++;
        return prefix + "_" + (labelIndex-1);
    }

    public void append(String instruction) {
        instructions.add(instruction);
    }

    public void popTo(String register) {
        instructions.add("lw " + register + " 4(sp)");
        instructions.add("addi sp sp 4");
    }

    public void pushFrom(String register) {
        instructions.add("addi sp sp -4");
        instructions.add("sw " + register + " 4(sp)");
    }

    public void moveValue(String fromRegister, String toRegister) {
        instructions.add("mv " + fromRegister + " " + toRegister);
    }

    public void exit() {
        instructions.add("li a0 10");
        instructions.add("ecall");
    }

    public void exit(int code) {
        instructions.add("lit a0 17");
        instructions.add("lit a1 " + code);
        instructions.add("ecall");
    }

    @Override
    public String toString() {
        String code = "";
        for(String instruction : instructions) {
            code += instruction;
            code += "\n";
        }
        return code;
    }

}
