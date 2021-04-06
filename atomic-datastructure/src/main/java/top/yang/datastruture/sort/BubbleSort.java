package top.yang.datastruture.sort;

import java.util.Arrays;
import java.util.Comparator;

/**
 * 冒泡排序
 */
public class BubbleSort extends AbstractSort {
    public static <T extends Comparable<T>> void bubbleSort(T[] arr) {
        for (int i = 1; i < arr.length; i++) {
            for (int j = 0; j < arr.length - i; j++) {
                if (arr[j].compareTo(arr[j + 1]) > 0) {
                    swap(arr, j, j + 1);
                }
            }
        }
    }

    public static <T> void bubbleSort(T[] arr, Comparator<? super T> comparator) {
        for (int i = 1; i < arr.length; i++) {
            for (int j = 0; j < arr.length - i; j++) {
                if (comparator.compare(arr[j], arr[j + 1]) > 0) {
                    swap(arr, j, j + 1);
                }
            }
        }
    }

    public static void main(String[] args) {
        Integer[] list = new Integer[15];
        for (int i = 0; i < 15; i++) {
            list[i] = (int) (Math.random() * 100);
        }
        bubbleSort(list);
        System.out.println(Arrays.toString(list));
    }
}
