package rx5600xt.sxqe.shifttofix;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

public class PlayerInteractListener implements Listener {

    private final ShiftToFix plugin;
    private int durabilityIncrease = 10;

    public PlayerInteractListener(ShiftToFix plugin) {
        this.plugin = plugin;
    }

    public void setDurabilityIncrease(int value) {
        durabilityIncrease = value;
    }

    public int getDurabilityIncrease() {
        return durabilityIncrease;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!plugin.isPluginEnabled()) {
            return;
        }

        Player player = event.getPlayer();

        if (!player.isSneaking() || !event.getAction().toString().contains("RIGHT_CLICK")) {
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
                    break;
                }
            }
        }
    }
}
