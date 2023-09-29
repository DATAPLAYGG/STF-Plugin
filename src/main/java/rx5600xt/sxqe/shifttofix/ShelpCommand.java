package rx5600xt.sxqe.shifttofix;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ShelpCommand implements CommandExecutor, TabCompleter {
    private final FileConfiguration config;

    public ShelpCommand(FileConfiguration config) {
        this.config = config;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("[STF] 该命令只能由玩家执行！");
            return true;
        }

        if (!sender.hasPermission("stf.help")) {
            sender.sendMessage("[STF] 你没有权限执行该命令！");
            return true;
        }

        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
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

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            //I do only this one code comments of course this is made by baidu translate
            // Provide tab completions for the first argument (sub-command)
            completions.add("help");
            // Add more sub-commands here if needed
        }

        // You can add additional tab completion logic for arguments after the sub-command
        // For example:
        // if (args.length == 2 && args[0].equalsIgnoreCase("someSubCommand")) {
        //     completions.add("option1");
        //     completions.add("option2");
        // }

        return completions;
    }
}
