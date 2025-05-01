package org.imperial.fastquantanalysis.strategy;

import lombok.NoArgsConstructor;
import org.imperial.fastquantanalysis.entity.QuantStrategy;

import java.util.Arrays;

@NoArgsConstructor
public class UserStrategy {

    public int[] twoSum(int[] nums, int target) {
        int[] result = new int[2];

        OUTER:
        for (int i = 0; i < nums.length; i++) {
            for (int j = i + 1; j < nums.length; j++) {
                if (nums[i] + nums[j] == target) {
                    result[0] = i;
                    result[1] = j;
                    break OUTER;
                }
            }
        }

        return result;
    }

    public static void main(String[] args) {
        int[] nums = new int[]{2, 7, 11, 15};
        UserStrategy userStrategy = new UserStrategy();
        int[] result = userStrategy.twoSum(nums, 9);
        System.out.println(Arrays.toString(result));
    }
}
