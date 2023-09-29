package rx5600xt.sxqe.shifttofix;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

public class PlayerInteractListener implements Listener {

    private final JavaPlugin plugin;
    private int durabilityIncrease = 10;
    private final Map<Player, Long> cooldowns = new HashMap<>();
    private long cooldownDuration = 10000;
    private final Map<Player, Integer> actionBarTaskIds = new HashMap<>();

    public PlayerInteractListener(JavaPlugin plugin) {
        this.plugin = plugin;
        loadConfigValues();
    }

    public void setDurabilityIncrease(int value) {
        durabilityIncrease = value;
    }

    public int getDurabilityIncrease() {
        return durabilityIncrease;
    }

    private void loadConfigValues() {
        FileConfiguration config = plugin.getConfig();
        cooldownDuration = config.getLong("Cooldown", 10000);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!plugin.isEnabled()) {
            return;
        }

        Player player = event.getPlayer();

        if (!player.isSneaking() || !event.getAction().toString().contains("RIGHT_CLICK")) {
            return;
        }

        if (hasCooldown(player)) {
            return;
        }

        ItemStack offhandItem = player.getInventory().getItemInOffHand();
        if (offhandItem.getType() != Material.IRON_INGOT) {
            return;
        }

        ItemStack[] armorContents = player.getInventory().getArmorContents();

        for (ItemStack armorItem : armorContents) {
            if (armorItem != null && armorItem.getItemMeta() instanceof Damageable) {
                Damageable damageable = (Damageable) armorItem.getItemMeta();

                if (damageable.hasDamage()) {
                    damageable.setDamage(damageable.getDamage() - durabilityIncrease);
                    armorItem.setItemMeta((ItemMeta) damageable);

                    offhandItem.setAmount(offhandItem.getAmount() - 1);
                    player.getInventory().setItemInOffHand(offhandItem);
                    player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_DESTROY, 1.0f, 1.0f);

                    startCooldown(player);

                    break;
                }
            }
        }
    }

    private boolean hasCooldown(Player player) {
        if (cooldowns.containsKey(player)) {
            long currentTime = System.currentTimeMillis();
            long cooldownTime = cooldowns.get(player);

            if (currentTime < cooldownTime) {
                return true;
            }
        }
        return false;
    }

    private void startCooldown(Player player) {
        long currentTime = System.currentTimeMillis();
        long cooldownTime = currentTime + cooldownDuration;
        cooldowns.put(player, cooldownTime);
        int taskId = new BukkitRunnable() {
            @Override
            public void run() {
                if (player.isOnline()) {
                    long remainingTime = cooldownTime - System.currentTimeMillis();
                    if (remainingTime <= 0) {
                        sendActionBar(player, ChatColor.GREEN + "冷却时间结束!");
                        actionBarTaskIds.remove(player);
                        this.cancel();
                    } else {
                        sendActionBar(player, ChatColor.YELLOW + "剩余冷却时间: " + ChatColor.RED + (remainingTime / 1000) + " 秒");
                    }
                } else {
                    actionBarTaskIds.remove(player);
                    this.cancel();
                }
            }
        }.runTaskTimer(plugin, 0, 20).getTaskId();

        actionBarTaskIds.put(player, taskId);
    }

    private void sendActionBar(Player player, String message) {
        player.sendActionBar(message);
    }

    public void setCooldownDuration(long duration) {
        cooldownDuration = duration;
    }
    public long getCooldownDuration() {
        return cooldownDuration;
    }


}

