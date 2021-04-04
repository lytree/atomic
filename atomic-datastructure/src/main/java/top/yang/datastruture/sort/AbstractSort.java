package top.yang.datastruture.sort;

public abstract class AbstractSort {
    static <T> void swap(T[] arr, int left, int right) {
        T temp = arr[left];
        arr[left] = arr[right];
        arr[right] = temp;
    }
}
