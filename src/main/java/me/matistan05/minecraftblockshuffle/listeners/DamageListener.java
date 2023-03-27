package me.matistan05.minecraftblockshuffle.listeners;

import me.matistan05.minecraftblockshuffle.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import static me.matistan05.minecraftblockshuffle.commands.BlockShuffleCommand.*;

public class DamageListener implements Listener {
    private final Main main;
    public DamageListener(Main main) {
        this.main = main;
        Bukkit.getPluginManager().registerEvents(this, main);
    }
    @EventHandler
    public void DamageEvent(EntityDamageByEntityEvent e) {
        if(e.getEntity() instanceof Player && e.getDamager() instanceof Player) {
            Player p = (Player) e.getEntity();
            if(inGame && players.stream().anyMatch(obj -> obj.getName().equals(p.getName())) && !main.getConfig().getBoolean("pvpEnabled")) {
                e.setCancelled(true);
            }
        }
    }
}