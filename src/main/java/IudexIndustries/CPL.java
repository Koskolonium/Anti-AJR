package IudexIndustries;

import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientKeepAlive;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerInput;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class CPL extends PacketListenerAbstract {
    private final ConcurrentHashMap<UUID, UserData> users;
    private final TransactionManager transactionManager;

    public CPL(ConcurrentHashMap<UUID, UserData> users, TransactionManager transactionManager) {
        super(PacketListenerPriority.HIGH);
        this.users = users;
        this.transactionManager = transactionManager;
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (!(event.getPacketType() instanceof PacketType.Play.Client type)) return;
        UUID uuid = event.getUser().getUUID();
        UserData data = users.get(uuid);
        if (data == null) return;
        switch (type) {
            case PLAYER_INPUT -> handlePlayerInput(event, data);
            case CLIENT_TICK_END -> handleTickEnd(data);
            case KEEP_ALIVE -> handleKeepAlive(event, uuid, data);
            default -> { }
        }
    }

    private void handlePlayerInput(PacketReceiveEvent event, UserData data) {
        WrapperPlayClientPlayerInput packet = new WrapperPlayClientPlayerInput(event);

        if (packet.isJump()) {
            if (!data.isCurrentlyJumping()) {
                data.setLatestJumpStart(System.nanoTime());
                data.setTicksSinceJumpStart(0);
                data.setCurrentlyJumping(true);
                data.setResetAttempt(data.isInHitTick());
            }
            return;
        }
        if (data.isCurrentlyJumping()) {
            data.setCurrentlyJumping(false);
            if (data.isResetAttempt()) {
                data.setResetAttempt(false);
                data.addSample(data.getTicksSinceJumpStart());
            }
        }
    }

    private void handleTickEnd(UserData data) {
        if (data.isCurrentlyJumping()) {
            data.setTicksSinceJumpStart(data.getTicksSinceJumpStart() + 1);
        }
        data.setInHitTick(false);
    }

    private void handleKeepAlive(PacketReceiveEvent event, UUID uuid, UserData data) {
        WrapperPlayClientKeepAlive packet = new WrapperPlayClientKeepAlive(event);
        Long sendTime = transactionManager.removeTransaction(uuid, packet.getId());
        if (sendTime == null) return;
        event.setCancelled(true);
        data.setPing(System.currentTimeMillis() - sendTime);
        data.setInHitTick(true);
    }
}