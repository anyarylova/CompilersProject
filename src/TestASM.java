import org.objectweb.asm.*;

public class TestASM implements Opcodes {
    public static void main(String[] args) {
        ClassWriter cw = new ClassWriter(0);
        System.out.println("ASM is working!");
    }
}
