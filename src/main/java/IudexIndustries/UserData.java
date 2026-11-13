package IudexIndustries;

import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class UserData {

    private volatile long latestJumpStart;
    private volatile boolean currentlyJumping = false;
    private volatile int ticksSinceJumpStart = 0;
    private final List<Integer> jumpTickCounts = new ArrayList<>();
    private final int windowSize;
    private final AJRCheck ajrCheck;
    private final AlertManager alertManager;
    private final String playerName;
    private float ping;
    private volatile boolean inHitTick = false;
    private volatile boolean resetAttempt = false;

    public UserData(int windowSize, AJRCheck ajrCheck, AlertManager alertManager, String playerName) {
        this.windowSize = windowSize;
        this.ajrCheck = ajrCheck;
        this.alertManager = alertManager;
        this.playerName = playerName;
    }

    public synchronized void addSample(int tickCount) {
        jumpTickCounts.add(tickCount);
        if (jumpTickCounts.size() >= windowSize) {
            List<Integer> snapshot = new ArrayList<>(jumpTickCounts);
            jumpTickCounts.clear();
            if (ajrCheck.isSuspicious(snapshot)) {
                alertManager.sendAlert(playerName, snapshot);

            }
        }
    }
}