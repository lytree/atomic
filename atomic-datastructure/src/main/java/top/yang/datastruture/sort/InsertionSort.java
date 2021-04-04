package top.yang.datastruture.sort;

import java.util.Arrays;
import java.util.Comparator;

public class InsertionSort extends AbstractSort {
    public static <T extends Comparable<T>> void insertionSort(T[] arr) {
        for (int i = 0; i < arr.length; i++) {
            int max = i;
            for (int j = i + 1; j < arr.length; j++) {
                if (arr[j].compareTo(arr[max]) >= 0) {
                    max = j;
                }
            }
            swap(arr, i, max);
        }
    }

    public static <T> void insertionSort(T[] arr, Comparator<T> comparator) {
        for (int i = 0; i < arr.length; i++) {
            int max = i;
            for (int j = i + 1; j < arr.length; j++) {
                if (comparator.compare(arr[max], arr[j]) <= 0) {
                    max = j;
                }
            }
            swap(arr, i, max);
        }
    }

    public static void main(String[] args) {
        Integer[] list = new Integer[15];
        for (int i = 0; i < 15; i++) {
            list[i] = (int) (Math.random() * 10);
        }
        insertionSort(list);
        System.out.println(Arrays.toString(list));
    }
}
