package rx5600xt.sxqe.shifttofix;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class STFSetCommand implements CommandExecutor {

    private final PlayerInteractListener listener;
    public STFSetCommand(PlayerInteractListener listener, ShiftToFix shiftToFix) {
        this.listener = listener;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("[STF] 该命令只能由玩家执行！");
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("shifttofix.setdurability")) {
            player.sendMessage("[STF] 你没有权限执行该命令！");
            return true;
        }

        if (args.length != 1) {
            return false;
        }

        try {
            int value = Integer.parseInt(args[0]);
            listener.setDurabilityIncrease(value);
            player.sendMessage("[STF] 成功将耐久值增加量设置为 " + value);
        } catch (NumberFormatException e) {
            player.sendMessage("[STF] 请输入一个有效的整数值！");
        }

        return true;
    }
}
