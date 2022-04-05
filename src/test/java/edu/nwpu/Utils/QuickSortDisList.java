package edu.nwpu.Utils;
import edu.nwpu.domain.Bus;
import java.util.LinkedList;


public class QuickSortDisList {

    /**
     * 将车辆列表快速排序
     * @param disList 与Bus对应的距离列表
     * @param busList 待排车辆列表
     * @param leftIndex 待排序列起始位置
     * @param rightIndex 待排序列结束位置
     */
    public static void quickSort(LinkedList<Double> disList, LinkedList<Bus> busList, int leftIndex, int rightIndex) {
        if (leftIndex >= rightIndex) {
            return;
        }

        int left = leftIndex;
        int right = rightIndex;
        double key = disList.get(left);
        Bus busKey = busList.get(left);

        while (left < right) {
            while (right > left && disList.get(right) >= key) {
                right--;
            }

            disList.set(left, disList.get(right));
            busList.set(left, busList.get(right));

            while (left < right && disList.get(left) <= key) {
                left++;
            }

            disList.set(right, disList.get(left));
            busList.set(right, busList.get(left));
        }
        busList.set(left, busKey);
        disList.set(left, key);

        quickSort(disList, busList, leftIndex, left - 1);
        quickSort(disList, busList, right + 1, rightIndex);
    }
}
