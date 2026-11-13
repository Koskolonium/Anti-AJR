package IudexIndustries;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDamageEvent;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class SPL extends PacketListenerAbstract {

    private final TransactionManager transactionManager;

    public SPL(TransactionManager transactionManager) {
        super(PacketListenerPriority.NORMAL);
        this.transactionManager = transactionManager;
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        if (event.getPacketType() == PacketType.Play.Server.DAMAGE_EVENT) {
            handleDamageEvent(event);
        }
    }

    private void handleDamageEvent(PacketSendEvent event) {
        WrapperPlayServerDamageEvent packet = new WrapperPlayServerDamageEvent(event);
        int causeId = packet.getSourceCauseId();
        if (causeId == 0) return;
        int attackerId = causeId - 1;
        Entity attacker = SpigotConversionUtil.getEntityById(null, attackerId);
        if (!(attacker instanceof Player)) return;
        Entity damaged = SpigotConversionUtil.getEntityById(null, packet.getEntityId());
        if (!(damaged instanceof Player victimPlayer)) return;
        User victim = PacketEvents.getAPI().getPlayerManager().getUser(victimPlayer);
        if (victim == null) return;
        event.getPostTasks().add(() -> transactionManager.sendKeepAlive(victim));
    }
}