package littlegruz.arpeegee.listeners;

import littlegruz.arpeegee.ArpeegeeMain;
import littlegruz.arpeegee.entities.RPGMagicPlayer;
import littlegruz.arpeegee.entities.RPGMeleePlayer;
import littlegruz.arpeegee.entities.RPGPlayer;
import littlegruz.arpeegee.entities.RPGRangedPlayer;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLevelChangeEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerLevel implements Listener{
   private ArpeegeeMain plugin;
   
   public PlayerLevel(ArpeegeeMain instance){
      plugin = instance;
   }

   // Give new weapons if the necessary level is reached
   @EventHandler
   public void onPlayerLevel(PlayerLevelChangeEvent event){
      if(plugin.getWorldsMap().containsKey(event.getPlayer().getWorld().getName())){
         if(event.getNewLevel() > event.getOldLevel())
            event.getPlayer().sendMessage("Level up!");
         
         // New weapon for the melee class
         if(plugin.getMeleePlayerMap().get(event.getPlayer().getName()) != null){
            RPGMeleePlayer rpgPlaya = plugin.getMeleePlayerMap().get(event.getPlayer().getName());
   
            // Increase player stats
            levelUp(rpgPlaya, event.getPlayer(), event.getNewLevel());

            // Blink
            if(rpgPlaya.getLevel() == 6)
               event.getPlayer().getInventory().setItem(1, new ItemStack(Material.RAW_FISH,1));
            // Silence
            else if(rpgPlaya.getLevel() == 11)
               event.getPlayer().getInventory().setItem(2, new ItemStack(Material.CARROT_ITEM,1));
            // Imobilise
            else if(rpgPlaya.getLevel() == 16)
               event.getPlayer().getInventory().setItem(3, new ItemStack(Material.PUMPKIN_PIE,1));
            // Bide
            else if(rpgPlaya.getLevel() == 20)
               event.getPlayer().getInventory().setItem(4, new ItemStack(Material.POTATO_ITEM,1));
         }
         // New weapon for the ranged class
         else if(plugin.getRangedPlayerMap().get(event.getPlayer().getName()) != null){
            RPGRangedPlayer rpgPlaya = plugin.getRangedPlayerMap().get(event.getPlayer().getName());
            
            // Increase player stats
            levelUp(rpgPlaya, event.getPlayer(), event.getNewLevel());
            
            // Create the base dye type first
            ItemStack is = new ItemStack(351,1);
            // Slow arrow
            if(rpgPlaya.getLevel() == 6){
               is.setDurability((short)12);
               event.getPlayer().getInventory().setItem(1, is);
            }
            // Blind arrow
            else if(rpgPlaya.getLevel() == 11){
               is.setDurability((short)0);
               event.getPlayer().getInventory().setItem(2, is);
            }
            // Eggsplode
            else if(rpgPlaya.getLevel() == 16)
               event.getPlayer().getInventory().setItem(3, new ItemStack(Material.EGG,1));
            // Sheep arrow
            else if(rpgPlaya.getLevel() == 20){
               is.setDurability((short)15);
               event.getPlayer().getInventory().setItem(4, is);
            }
         }
         // New weapons for the magic class
         else if(plugin.getMagicPlayerMap().get(event.getPlayer().getName()) != null){
            RPGMagicPlayer rpgPlaya = plugin.getMagicPlayerMap().get(event.getPlayer().getName());
            
            // Increase player stats
            levelUp(rpgPlaya, event.getPlayer(), event.getNewLevel());
            
            // Create the base dye type first
            ItemStack is = new ItemStack(351,1);
            // Fire
            if(rpgPlaya.getLevel() == 6){
               is.setDurability((short)1);
               event.getPlayer().getInventory().setItem(1, is);
            }
            // Confusion
            else if(rpgPlaya.getLevel() == 11){
               is.setDurability((short)14);
               event.getPlayer().getInventory().setItem(2, is);
            }
            // Heal
            else if(rpgPlaya.getLevel() == 16){
               is.setDurability((short)15);
               event.getPlayer().getInventory().setItem(3, is);
            }
            // Sheep summon
            else if(rpgPlaya.getLevel() == 20){
               is.setType(Material.WHEAT);
               event.getPlayer().getInventory().setItem(4, is);
            }
         }
      }
   }
   
   private void levelUp(RPGPlayer rpgPlaya, Player playa, int newLevel){

      if(newLevel > plugin.MAX_LEVEL){
         rpgPlaya.setLevel(plugin.MAX_LEVEL);
         playa.setLevel(plugin.MAX_LEVEL);
      }
      else
         rpgPlaya.setLevel(newLevel);
   }
}
