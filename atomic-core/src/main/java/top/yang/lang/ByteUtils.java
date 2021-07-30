package top.yang.lang;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

public class ByteUtils {

  /**
   * double数组转小端byte数组
   *
   * @param data double数组
   * @return 小端字节数组
   */
  public static byte[] arrayToByteArrayLittle(double[] data) {
    ByteBuffer byteBuffer = ByteBuffer.allocate(8 * data.length);
    byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    for (int i = 0; i < data.length; i++) {
      byteBuffer.putDouble(data[i]);
    }
    return byteBuffer.array();
  }

  /**
   * float数组转小端byte数组
   *
   * @param data float数组
   * @return 小端字节数组
   */
  public static byte[] arrayToByteArrayLittle(float[] data) {
    ByteBuffer byteBuffer = ByteBuffer.allocate(4 * data.length);
    byteBuffer.order(ByteOrder.LITTLE_ENDIAN);

    for (int i = 0; i < data.length; i++) {
      byteBuffer.putFloat(data[i]);
    }
    return byteBuffer.array();
  }


  /**
   * int数组转小端byte数组
   *
   * @param data double数组
   * @return 小端字节数组
   */
  public static byte[] arrayToByteArrayLittle(int[] data) {
    ByteBuffer byteBuffer = ByteBuffer.allocate(4 * data.length);
    byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    for (int i = 0; i < data.length; i++) {
      byteBuffer.putInt(data[i]);
    }
    return byteBuffer.array();
  }

  /**
   * short数组转小端byte数组
   *
   * @param data double数组
   * @return 小端字节数组
   */
  public static byte[] arrayToByteArrayLittle(short[] data) {
    ByteBuffer byteBuffer = ByteBuffer.allocate(2 * data.length);
    byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    for (int i = 0; i < data.length; i++) {
      byteBuffer.putShort(data[i]);
    }
    return byteBuffer.array();
  }

  /**
   * double数组转大端byte数组
   *
   * @param data float数组
   * @return 小端字节数组
   */
  public static byte[] arrayToByteArrayBig(double[] data) {
    ByteBuffer byteBuffer = ByteBuffer.allocate(4 * data.length);
    byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    for (int i = 0; i < data.length; i++) {
      byteBuffer.putDouble(data[i]);
    }
    return byteBuffer.array();
  }

  /**
   * float数组转大端byte数组
   *
   * @param data float数组
   * @return 小端字节数组
   */
  public static byte[] arrayToByteArrayBig(float[] data) {
    ByteBuffer byteBuffer = ByteBuffer.allocate(4 * data.length);
    byteBuffer.order(ByteOrder.BIG_ENDIAN);
    for (int i = 0; i < data.length; i++) {
      byteBuffer.putFloat(data[i]);
    }
    return byteBuffer.array();
  }


  /**
   * int数组转大端byte数组
   *
   * @param data float数组
   * @return 小端字节数组
   */
  public static byte[] arrayToByteArrayBig(int[] data) {
    ByteBuffer byteBuffer = ByteBuffer.allocate(4 * data.length);
    byteBuffer.order(ByteOrder.BIG_ENDIAN);
    for (int i = 0; i < data.length; i++) {
      byteBuffer.putInt(data[i]);
    }
    return byteBuffer.array();
  }

  /**
   * short数组转大端byte数组
   *
   * @param data float数组
   * @return 小端字节数组
   */
  public static byte[] arrayToByteArrayBig(short[] data) {
    ByteBuffer byteBuffer = ByteBuffer.allocate(2 * data.length);
    byteBuffer.order(ByteOrder.BIG_ENDIAN);
    for (int i = 0; i < data.length; i++) {
      byteBuffer.putShort(data[i]);
    }
    return byteBuffer.array();
  }

  /**
   * 小端byte数组 转 int数组
   *
   * @param data 小端byte数组
   * @return double数组
   */
  public static short[] byteArrayToShortArrayLittle(byte[] data) {
    short[] result = new short[data.length / 2];
    ByteBuffer byteBuffer = ByteBuffer.allocate(data.length);
    byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    byteBuffer.put(data);
    for (int i = 0; i < data.length; i += 2) {
      result[i / 2] = data[i];
    }
    return result;
  }

  /**
   * 小端byte数组 转 int数组
   *
   * @param data 小端byte数组
   * @return double数组
   */
  public static int[] byteArrayToIntArrayLittle(byte[] data) {
    int[] result = new int[data.length / 4];
    ByteBuffer byteBuffer = ByteBuffer.allocate(data.length);
    byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    byteBuffer.put(data);
    for (int i = 0; i < data.length; i += 4) {
      result[i / 4] = data[i];
    }
    return result;
  }

  /**
   * 小端byte数组转float数组
   *
   * @param data 小端byte数组
   * @return float数组
   */
  public static float[] byteArrayToFloatArrayLittle(byte[] data) {
    float[] result = new float[data.length / 4];
    ByteBuffer byteBuffer = ByteBuffer.allocate(data.length);
    byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    byteBuffer.put(data);
    for (int i = 0; i < data.length; i += 4) {
      result[i / 4] = data[i];
    }
    return result;
  }

  /**
   * 小端byte数组 转 double数组
   *
   * @param data 小端byte数组
   * @return double数组
   */
  public static double[] byteArrayToDoubleArrayLittle(byte[] data) {
    double[] result = new double[data.length / 8];
    ByteBuffer byteBuffer = ByteBuffer.allocate(data.length);
    byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    byteBuffer.put(data);
    for (int i = 0; i < data.length; i += 8) {
      result[i / 8] = data[i];
    }
    return result;
  }
}