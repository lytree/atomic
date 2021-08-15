package top.yang.demo;

import java.util.ArrayList;

public class JvmOOM {

  static class OOMObject {

  }

  public static void main(String[] args) {
    ArrayList<OOMObject> list = new ArrayList<>();
    while (true) {
      list.add(new OOMObject());
    }
  }
}
