package IudexIndustries;

import java.util.List;

public class AJRCheck {
    private static final int THRESHOLD_COUNT = 3;

    public boolean isSuspicious(List<Integer> samples) {
        int ones = 0;
        for (int tickCount : samples) {
            if (tickCount == 1 && ++ones >= THRESHOLD_COUNT) {
                return true;
            }
        }
        return false;
    }
}