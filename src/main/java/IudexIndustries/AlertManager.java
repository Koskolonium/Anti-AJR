package IudexIndustries;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class AlertManager {
    public static final String PERMISSION = "iudex.ajrnotifications";
    private final JavaPlugin plugin;
    private final ConcurrentHashMap<UUID, UserData> users;

    public AlertManager(JavaPlugin plugin, ConcurrentHashMap<UUID, UserData> users) {
        this.plugin = plugin;
        this.users = users;
    }

    public void sendAlert(String suspectName, List<Integer> window) {
        Bukkit.getScheduler().runTask(plugin, () -> {
            String message = buildMessage(suspectName, window);
            for (UUID id : users.keySet()) {
                Player staff = Bukkit.getPlayer(id);
                if (staff == null || !staff.hasPermission(PERMISSION)) continue;
                staff.sendMessage(message);
            }
            plugin.getLogger().warning(ChatColor.stripColor(message).replace("\n", " | "));
        });
    }

    private String buildMessage(String suspectName, List<Integer> window) {
        String line = ChatColor.DARK_GRAY + "" + ChatColor.STRIKETHROUGH + "                                        ";
        return line + "\n"
                + ChatColor.RED + "" + ChatColor.BOLD + "⚠ Iudex " + ChatColor.GRAY + "» "
                + ChatColor.YELLOW + "AJR suspected\n"
                + ChatColor.GRAY + " Player: " + ChatColor.WHITE + suspectName + "\n"
                + ChatColor.GRAY + " Jump ticks: " + ChatColor.AQUA + window + "\n"
                + line;
    }
}