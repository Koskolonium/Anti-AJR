package IudexIndustries;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.UserDisconnectEvent;
import com.github.retrooper.packetevents.event.UserLoginEvent;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class Main extends JavaPlugin {
    private static final int WINDOW_SIZE = 10;
    private final ConcurrentHashMap<UUID, UserData> connectedUsers = new ConcurrentHashMap<>();
    private final AJRCheck ajrCheck = new AJRCheck();
    private AlertManager alertManager;
    private CPL cpl;
    private SPL spl;
    private PacketListenerAbstract connectionListener;
    private TransactionManager transactionManager;

    @Override
    public void onLoad() {
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));
        PacketEvents.getAPI().load();
    }

    @Override
    public void onEnable() {
        PacketEvents.getAPI().init();
        alertManager = new AlertManager(this, connectedUsers);
        transactionManager = new TransactionManager();
        cpl = new CPL(connectedUsers, transactionManager);
        spl = new SPL(transactionManager);
        connectionListener = new PacketListenerAbstract() {

            @Override
            public void onUserLogin(@NotNull UserLoginEvent event) {
                UUID uuid = event.getUser().getUUID();
                connectedUsers.put(uuid, new UserData(
                        WINDOW_SIZE,
                        ajrCheck,
                        alertManager,
                        event.getUser().getName()
                ));
            }

            @Override
            public void onUserDisconnect(@NotNull UserDisconnectEvent event) {
                UUID uuid = event.getUser().getUUID();
                connectedUsers.remove(uuid);
                transactionManager.removePlayer(uuid);
            }
        };
        PacketEvents.getAPI().getEventManager().registerListener(cpl);
        PacketEvents.getAPI().getEventManager().registerListener(spl);
        PacketEvents.getAPI().getEventManager().registerListener(connectionListener);
    }

    @Override
    public void onDisable() {
        PacketEvents.getAPI().getEventManager().unregisterListener(cpl);
        PacketEvents.getAPI().getEventManager().unregisterListener(spl);
        PacketEvents.getAPI().getEventManager().unregisterListener(connectionListener);
        connectedUsers.clear();
        transactionManager.getTransactions().clear();
        PacketEvents.getAPI().terminate();
    }
}