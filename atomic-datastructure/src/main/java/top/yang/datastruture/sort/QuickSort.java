package top.yang.datastruture.sort;

import java.util.Arrays;
import java.util.Comparator;

/**
 * 快速排序
 */
public class QuickSort extends AbstractSort {
    public static <T extends Comparable<T>> void quickSort(T[] arr) {
        quickSort(arr, 0, arr.length - 1);
    }

    public static <T> void quickSort(T[] arr, Comparator<? super T> comparator) {
        quickSort(arr, 0, arr.length - 1, comparator);
    }

    private static <T extends Comparable<T>> void quickSort(T[] arr, int left, int right) {
        if (left >= right) return;
        int pivotIndex = partition(arr, left, right);
        quickSort(arr, left, pivotIndex - 1);
        quickSort(arr, pivotIndex + 1, right);

    }

    private static <T> void quickSort(T[] list, int left, int right, Comparator<? super T> comparator) {
        if (left >= right) return;
        int pivotIndex = partition(list, left, right, comparator);
        quickSort(list, left, pivotIndex - 1, comparator);
        quickSort(list, pivotIndex + 1, right, comparator);
    }

    private static <T extends Comparable<T>> int partition(T[] arr, int left, int right) {
        int start = left;
        int end = right + 1;
        T t = arr[start];
        while (true) {
            //找到比他大的或者相等的  （他比t小就继续直到找到比他大或者相等的）
            while (arr[++start].compareTo(t) <= 0) ;
            //找到比他小的或者相等的   （他比t大就继续直到找到比他小或者相等的）
            while (arr[--end].compareTo(t) > 0) ;
            if (start >= end) {
                break;
            }
            swap(arr, start, end);
        }
        swap(arr, left, end);
        return end;
    }

    private static <T> int partition(T[] arr, int left, int right, Comparator<? super T> comparator) {
        int start = left;
        int end = right + 1;
        T t = arr[start];
        while (true) {

            //找到比他大的或者相等的  （他比t小就继续直到找到比他大或者相等的）
            while (comparator.compare(arr[++start], t) <= 0) ;
            //找到比他小的或者相等的   （他比t大就继续直到找到比他小或者相等的）
            while (comparator.compare(arr[--end], t) > 0) ;
            if (start >= end) {
                break;
            }
            swap(arr, start, end);
        }
        swap(arr, left, end);
        return end;
    }


    public static void main(String[] args) {
        Integer[] list = new Integer[15];
        for (int i = 0; i < 15; i++) {
            list[i] = (int) (Math.random() * 10);
        }
        quickSort(list);
        System.out.println(Arrays.toString(list));
    }
}
