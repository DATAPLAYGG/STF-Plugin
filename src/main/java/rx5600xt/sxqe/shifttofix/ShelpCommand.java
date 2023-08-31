package rx5600xt.sxqe.shifttofix;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

public class ShelpCommand implements CommandExecutor {
    private final FileConfiguration config;

    public ShelpCommand(PlayerInteractListener playerInteractListener, FileConfiguration config) {
        this.config = config;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("stf.help")) {
            sender.sendMessage("[STF] 你没有权限执行该命令！");
            return true;
        }

        if (args.length > 0 && args[0].equalsIgnoreCase("help")) {
            String helpMessage = config.getString("helpMessage");
            if (helpMessage != null && !helpMessage.isEmpty()) {
                sender.sendMessage(helpMessage);
            } else {
                sender.sendMessage("No help message configured.");
            }
            return true;
        }

        return false;
    }
}
