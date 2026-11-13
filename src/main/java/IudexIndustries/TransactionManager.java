package IudexIndustries;

import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerKeepAlive;
import lombok.Getter;
import lombok.Setter;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

@Getter
@Setter
public class TransactionManager {
    private static final long EXPIRY_MILLIS = 10_000L;
    private static final int MAX_PENDING = 64;

    private final ConcurrentHashMap<UUID, ConcurrentHashMap<Long, Long>> transactions = new ConcurrentHashMap<>();

    public void sendKeepAlive(User user) {
        UUID uuid = user.getUUID();
        if (uuid == null) return;
        ConcurrentHashMap<Long, Long> playerTransactions = transactions.computeIfAbsent(
                uuid, ignored -> new ConcurrentHashMap<>()
        );
        long sendTime = System.currentTimeMillis();
        playerTransactions.values().removeIf(sentAt -> sendTime - sentAt > EXPIRY_MILLIS);
        if (playerTransactions.size() >= MAX_PENDING) return;
        long transactionId;
        do {
            transactionId = -1L - (ThreadLocalRandom.current().nextLong() & Long.MAX_VALUE);
        } while (playerTransactions.putIfAbsent(transactionId, sendTime) != null);

        try {
            user.sendPacket(new WrapperPlayServerKeepAlive(transactionId));
        } catch (Exception exception) {
            playerTransactions.remove(transactionId);
            throw exception;
        }
    }

    public Long removeTransaction(UUID uuid, long transactionId) {
        ConcurrentHashMap<Long, Long> playerTransactions = transactions.get(uuid);
        if (playerTransactions == null) return null;
        return playerTransactions.remove(transactionId);
    }

    public void removePlayer(UUID uuid) {
        transactions.remove(uuid);
    }
}