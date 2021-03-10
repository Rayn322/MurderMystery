package com.ryan.murdermystery.listeners;

import com.destroystokyo.paper.event.entity.ProjectileCollideEvent;
import com.ryan.murdermystery.Gameplay;
import com.ryan.murdermystery.scoreboards.HideNametags;
import com.ryan.murdermystery.MurderMystery;
import com.ryan.murdermystery.utils.MMUtils;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.util.Ticks;
import org.bukkit.ChatColor;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Painting;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupArrowEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

public class Listeners implements Listener {
    
    @EventHandler
    public void onPlayerAttack(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player) || !(event.getDamager() instanceof Player) && !(event.getDamager() instanceof Projectile))
            return;
        event.setCancelled(true);
        if (!MurderMystery.isPlaying) return;
        
        Player damager;
        Player attacked = (Player) event.getEntity();
        Location deathLocation = attacked.getLocation();
        
        if (event.getDamager() instanceof Player) {
            damager = (Player) event.getDamager();
        } else {
            Projectile arrow = (Projectile) event.getDamager();
            damager = (Player) arrow.getShooter();
        }
        
        // detective attacks
        if (MMUtils.isDetective(damager) && event.getCause() == EntityDamageEvent.DamageCause.PROJECTILE) {
            
            // detective shoots innocent
            if (MMUtils.isInnocent(attacked)) {
                Gameplay.kill(attacked);
                attacked.sendTitle(ChatColor.GOLD + "YOU DIED!",
                        ChatColor.WHITE + "You were shot by the detective.",
                        0,
                        60,
                        10);
                
                Gameplay.kill(damager);
                damager.sendTitle(ChatColor.RED + "YOU DIED!",
                        ChatColor.WHITE + "You shot an innocent player.",
                        0,
                        60,
                        10);
                System.out.println("detective shoot innocent");
                Gameplay.checkWinConditions();
                
            } else if (MMUtils.isMurderer(attacked)) {
                // detective shoots murderer
                Gameplay.kill(attacked);
                attacked.sendTitle(ChatColor.RED + "YOU DIED!",
                        ChatColor.WHITE + "You were shot by the detective.",
                        0,
                        60,
                        10);
                System.out.println("detective shot murderer");
                Gameplay.checkWinConditions();
            }
            
        } else if (MMUtils.isMurderer(damager) && damager.getInventory().getItemInMainHand().getType() == Material.IRON_SWORD) {
            // murderer attacks
            
            // murderer kills innocent
            if (MMUtils.isInnocent(attacked)) {
                Sound deathSound = Sound.sound(Key.key("entity.player.hurt"), Sound.Source.MASTER, 2, 1);
                MurderMystery.world.playSound(deathSound);
                System.out.println("murderer kill innocent");
                
                Gameplay.kill(attacked);
                attacked.sendTitle(ChatColor.GOLD + "YOU DIED!",
                        ChatColor.WHITE + "You were killed by the murderer.",
                        0,
                        60,
                        10);
                
                Gameplay.checkWinConditions();
            } else if (MMUtils.isDetective(attacked)) {
                
                // murderer kills detective
                Gameplay.kill(attacked);
                MurderMystery.world.dropItem(deathLocation, new ItemStack(Material.BOW));
                System.out.println("murderer kills detective");
                
                Component titleComponent = Component.text("The bow has been dropped!", TextColor.color(7, 212, 0));
                Component subtitleComponent = Component.text("Go pick it up to become the detective.", TextColor.color(83, 201, 79));
                Title.Times time = Title.Times.of(Ticks.duration(0), Ticks.duration(60), Ticks.duration(0));
                Title title = Title.title(titleComponent, subtitleComponent, time);
                MurderMystery.world.showTitle(title);
                
                Gameplay.checkWinConditions();
            }
        } else if (MMUtils.isInnocent(damager) && event.getCause() == EntityDamageEvent.DamageCause.PROJECTILE) {
            // innocent attacks
            
            // innocent shoots innocent
            if (MMUtils.isInnocent(attacked)) {
                Gameplay.kill(attacked);
                attacked.sendTitle(ChatColor.GOLD + "YOU DIED!",
                        ChatColor.WHITE + "You were shot by an innocent player.",
                        0,
                        60,
                        10);
                
                Gameplay.kill(damager);
                damager.sendTitle(ChatColor.RED + "YOU DIED!",
                        ChatColor.WHITE + "You shot an innocent player.",
                        0,
                        60,
                        10);
                System.out.println("innocent shoot innocent");
                Gameplay.checkWinConditions();
                
            } else if (MMUtils.isMurderer(attacked)) {
                // innocent shoots murderer
                Gameplay.kill(attacked);
                attacked.sendTitle(ChatColor.RED + "YOU DIED!",
                        ChatColor.WHITE + "You were shot by an innocent player.",
                        0,
                        60,
                        10);
                System.out.println("innocent shot murderer");
                Gameplay.checkWinConditions();
            }
        }
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        
        MurderMystery.world = player.getWorld();
        MurderMystery.world.setGameRule(GameRule.FALL_DAMAGE, false);
    
        HideNametags.joinTeam(player);
    }
    
    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        HideNametags.leaveTeam(event.getPlayer());
    }
    
    @EventHandler
    public void onPlayerLoseHealth(EntityDamageEvent event) {
        event.setDamage(0);
    }
    
    @EventHandler
    public void onPlayerPickupItem(EntityPickupItemEvent event) {
        if (event.getEntity() instanceof Player) {
            if (MMUtils.isMurderer((Player) event.getEntity())) {
                event.setCancelled(true);
            } else if (MMUtils.isInnocent((Player) event.getEntity()) && event.getItem().getItemStack().getType() == Material.BOW) {
                Player player = (Player) event.getEntity();
                Gameplay.giveArrows(player);
    
                Component titleComponent = Component.text("The bow has been picked up!", TextColor.color(7, 212, 0));
                Component subtitleComponent = Component.text("");
                Title.Times time = Title.Times.of(Ticks.duration(0), Ticks.duration(60), Ticks.duration(0));
                Title title = Title.title(titleComponent, subtitleComponent, time);
                MurderMystery.world.showTitle(title);
            }
        }
    }
    
    @EventHandler
    public void onPlayerPickupArrow(PlayerPickupArrowEvent event) {
        event.setCancelled(true);
    }
    
    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        if (event.getEntity() instanceof Arrow && !(event.getHitEntity() instanceof Painting)) {
            event.getEntity().remove();
        }
    }
    
    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        event.setCancelled(true);
    }
    
    @EventHandler
    public void onPaintingHit(ProjectileCollideEvent event) {
        if (event.getCollidedWith() instanceof Painting) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.getBlock().getType() == Material.PAINTING) {
            event.setCancelled(true);
        }
    }
}