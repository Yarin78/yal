package yarin.yal;

public class FixedRangeMinimumQuery {
    /**
     * Finds the minimum value in each range [0..range-1], [1..range], [2..range+1] etc
     */
    public int[] findMinimum(int[] data, int range) {
        if (range > data.length)
            throw new IllegalArgumentException();
        int[] result = new int[data.length - range + 1];
        for (int i = 0; i + range - 1 < data.length; i += range) {
            int[] minLeft = new int[range];
            minLeft[0] = data[i + range - 1];
            for (int j = 1; j < range; j++)	{
                int dif = minLeft[j - 1] - data[i + range - 1 - j];
                minLeft[j] = dif < 0 ? minLeft[j - 1] : data[i + range - 1 - j];
            }
            int minRight = data[i + range - 1];
            for (int j = 0; j < range && i + j < result.length; j++) {
                int dif = minLeft[range - j - 1] - minRight;
                result[i + j] = dif < 0 ? minLeft[range - j - 1] : minRight;
                if (i + range + j < data.length) {
                    dif = minRight - data[i + range + j];
                    if (dif > 0)
                        minRight = data[i + range + j];
                }
            }
        }

        return result;
    }
}
