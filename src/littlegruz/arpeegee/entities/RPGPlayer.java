package littlegruz.arpeegee.entities;

import org.bukkit.Material;
import org.bukkit.inventory.PlayerInventory;

public class RPGPlayer {
   private String name, incomplete, complete, party;
   private int level, chat;
   private float gear;
   private boolean move, baseAttack;

   // New RPGPlayer
   public RPGPlayer(String name){
      this.name = name;
      level = 1;
      gear = 0;
      chat = 0;
      incomplete = "none";
      complete = "none";
      party = "none";
      move = true;
   }
   
   // Restoring an RPGPlayer from a saved state
   public RPGPlayer(String name, int level, float gear, String incomplete, String complete, String party){
      this.name = name;
      this.level = level;
      this.gear = gear;
      this.incomplete = incomplete;
      this.complete = complete;
      this.party = party;
      move = true;
      chat = 0;
   }

   public String getName() {
      return name;
   }
   
   public int getLevel(){
      return level;
   }
   
   public void setLevel(int lvl){
      level = lvl;
   }
   
   public float getGearLevel(){
      return gear;
   }
   
   public void setGearLevel(float gear){
      this.gear = gear;
   }
   
   public void calcGearLevel(PlayerInventory playerInv){
      float newGear = 0;
      
      if(playerInv.getHelmet() != null){
         if(playerInv.getHelmet().getType().compareTo(Material.LEATHER_HELMET) == 0)
            newGear = 1;
         else if(playerInv.getHelmet().getType().compareTo(Material.IRON_HELMET) == 0)
            newGear = 2;
         else if(playerInv.getHelmet().getType().compareTo(Material.GOLD_HELMET) == 0)
            newGear = 3;
         else if(playerInv.getHelmet().getType().compareTo(Material.CHAINMAIL_HELMET) == 0)
            newGear = 3;
         else if(playerInv.getHelmet().getType().compareTo(Material.DIAMOND_HELMET) == 0)
            newGear = 4;
      }

      if(playerInv.getChestplate() != null){
         if(playerInv.getChestplate().getType().compareTo(Material.LEATHER_CHESTPLATE) == 0)
            newGear += 1;
         else if(playerInv.getChestplate().getType().compareTo(Material.IRON_CHESTPLATE) == 0)
            newGear += 2;
         else if(playerInv.getChestplate().getType().compareTo(Material.GOLD_CHESTPLATE) == 0)
            newGear += 3;
         else if(playerInv.getChestplate().getType().compareTo(Material.CHAINMAIL_CHESTPLATE) == 0)
            newGear += 3;
         else if(playerInv.getChestplate().getType().compareTo(Material.DIAMOND_CHESTPLATE) == 0)
            newGear += 4;
      }

      if(playerInv.getLeggings() != null){
         if(playerInv.getLeggings().getType().compareTo(Material.LEATHER_LEGGINGS) == 0)
            newGear += 1;
         else if(playerInv.getLeggings().getType().compareTo(Material.IRON_LEGGINGS) == 0)
            newGear += 2;
         else if(playerInv.getLeggings().getType().compareTo(Material.GOLD_LEGGINGS) == 0)
            newGear += 3;
         else if(playerInv.getLeggings().getType().compareTo(Material.CHAINMAIL_LEGGINGS) == 0)
            newGear += 3;
         else if(playerInv.getLeggings().getType().compareTo(Material.DIAMOND_LEGGINGS) == 0)
            newGear += 4;
      }
      
      if(playerInv.getBoots() != null){
         if(playerInv.getBoots().getType().compareTo(Material.LEATHER_BOOTS) == 0)
            newGear += 1;
         else if(playerInv.getBoots().getType().compareTo(Material.IRON_BOOTS) == 0)
            newGear += 2;
         else if(playerInv.getBoots().getType().compareTo(Material.GOLD_BOOTS) == 0)
            newGear += 3;
         else if(playerInv.getBoots().getType().compareTo(Material.CHAINMAIL_BOOTS) == 0)
            newGear += 3;
         else if(playerInv.getBoots().getType().compareTo(Material.DIAMOND_BOOTS) == 0)
            newGear += 4;
      }
      
      // Average out the values
      newGear /= 4;
      // Give value range to all the max to match the original Minecraft sword damage
      newGear *= 2;
      
      //Only update gear if the new value is larger
      if(newGear > gear)
         gear = newGear;
   }

   public String getParty(){
      return party;
   }

   public void setParty(String party){
      this.party = party;
   }

   public String getIncomplete(){
      return incomplete;
   }

   public void setIncomplete(String incomplete){
      this.incomplete = incomplete;
   }

   public String getComplete(){
      return complete;
   }

   public void setComplete(String complete){
      this.complete = complete;
   }

   public boolean canMove(){
      return move;
   }

   public void setMove(boolean move){
      this.move = move;
   }

   public boolean isBaseAttackReady(){
      return baseAttack;
   }

   public void setBaseAttackReadiness(boolean baseAttack){
      this.baseAttack = baseAttack;
   }

   /* 0 is normal global chat; 1 is party chat*/
   public int getChat(){
      return chat;
   }

   public void setChat(int chat){
      this.chat = chat;
   }
}
