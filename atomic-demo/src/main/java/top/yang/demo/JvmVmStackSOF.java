package top.yang.demo;

public class JvmVmStackSOF {

  private int stackLength = 1;

  public void stackLeak() {
    stackLength++;
    stackLeak();
  }

  public static void main(String[] args) {
    JvmVmStackSOF jvmVmStackSOF = new JvmVmStackSOF();
    try {
      jvmVmStackSOF.stackLeak();
    } catch (Throwable throwable) {
      System.out.println(jvmVmStackSOF.stackLength);
      throw throwable;
    }
  }
}
