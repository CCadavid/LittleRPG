package littlegruz.arpeegee.listeners;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.StringTokenizer;
import java.util.UUID;

import littlegruz.arpeegee.ArpeegeeMain;
import littlegruz.arpeegee.entities.RPGMagicPlayer;
import littlegruz.arpeegee.entities.RPGMeleePlayer;
import littlegruz.arpeegee.entities.RPGPlayer;
import littlegruz.arpeegee.entities.RPGQuest;

import org.bukkit.Effect;
import org.bukkit.EntityEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.BlockIterator;

public class PlayerInteract implements Listener{
   private ArpeegeeMain plugin;
   
   public PlayerInteract(ArpeegeeMain instance){
      plugin = instance;
   }
   
   @EventHandler
   public void onPlayerInteract(PlayerInteractEvent event){
      if(plugin.getWorldsMap().containsKey(event.getPlayer().getWorld().getName())){
         Player playa = event.getPlayer();
   
         // Casting weapon for "Flash"
         if(playa.getItemInHand().getType().compareTo(Material.RAW_FISH) == 0
               && event.getAction().toString().contains("RIGHT_CLICK")
               && plugin.getMeleePlayerMap().get(playa.getName()) != null
               && playa.getLevel() >= 0){ //TODO changed level for testing
            RPGMeleePlayer rpgm = plugin.getMeleePlayerMap().get(playa.getName());
            
            event.setCancelled(true);
            
            if(!rpgm.isFlashReady()){
               playa.sendMessage("Flash is still on cooldown");
               return;
            }
            
            HashSet<Byte> hs = new HashSet<Byte>();
            float gear;
            Block block;
            Location loc;
            
            rpgm.calcGearLevel(playa.getInventory());
            gear = rpgm.getGearLevel();
      
            hs.add((byte)0); //Air
            hs.add((byte)8); //Flowing water
            hs.add((byte)9); //Stationary water
            hs.add((byte)20); //Glass
            hs.add((byte)101); //Iron bar
            hs.add((byte)102); //Glass pane
            
            block = playa.getTargetBlock(hs, (int)(3 * gear)); //TODO change to appropriate block
            loc = block.getLocation();
            
            //playa.sendMessage(block.getType().toString());
            
            if(block.getType().compareTo(Material.AIR) != 0
                  && block.getType().compareTo(Material.WATER) != 0
                  && block.getType().compareTo(Material.STATIONARY_WATER) != 0
                  && block.getType().compareTo(Material.GLASS) != 0
                  && block.getType().compareTo(Material.THIN_GLASS) != 0
                  && block.getType().compareTo(Material.IRON_FENCE) != 0){
               loc.setY(loc.getY() + 1.5);
               
               if(loc.getBlock().getType().compareTo(Material.WATER) == 0
                     || loc.getBlock().getType().compareTo(Material.STATIONARY_WATER) == 0
                     || loc.getBlock().getType().compareTo(Material.AIR) == 0){
                  playa.teleport(new Location(loc.getWorld(), loc.getX(),
                        loc.getY(), loc.getZ(), playa.getLocation().getYaw(),
                        playa.getLocation().getPitch()));
                  playa.sendMessage("*Zoom*");
                  
                  // Give cooldown and remove item from inventory
                  /*playa.getInventory().remove(is);
                  ItemStack is = new ItemStack(351,1);
                  is.setDurability((short)13);*/
                  removeItem(playa);
                  plugin.giveCooldown(playa, "flash", "melee", 10);
                  plugin.getMeleePlayerMap().get(playa.getName()).setFlashReadiness(false);
               }
               else
                  playa.sendMessage("You can not flash to there");
            }
            else
               playa.sendMessage("You can not flash that far!");
         }
         else if(playa.getItemInHand().getType().compareTo(Material.POTATO_ITEM) == 0
               && event.getAction().toString().contains("RIGHT_CLICK")
               && plugin.getMeleePlayerMap().get(playa.getName()) != null
               && playa.getLevel() >= 0){ //TODO give skill level cap
            final String name = playa.getName();
            int taskID;
            
            if(!plugin.getMeleePlayerMap().get(playa.getName()).isBideReady()){
               playa.sendMessage("Bide is still on cooldown");
               return;
            }
            
            if(plugin.getBideMap().get(name) == null){
               taskID = plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                  public void run(){
                     for(Entity victim : plugin.getServer().getPlayer(name).getNearbyEntities(5, 5, 5)){
                        if(plugin.isEnemy(victim)){
                           if(victim instanceof LivingEntity){
                              ((LivingEntity) victim).damage(plugin.getMeleePlayerMap().get(name).getBideAmt());//TODO damage
                              ((LivingEntity) victim).playEffect(EntityEffect.HURT);
                           }
                        }
                     }
                     plugin.getServer().getPlayer(name).getWorld().playEffect(plugin.getServer().getPlayer(name).getLocation(), Effect.BLAZE_SHOOT, null);
                     plugin.getBideMap().remove(name);
                     plugin.getServer().getPlayer(name).sendMessage("Bide unleased!");
                  }
               }, 100L /* * rpgmp.getGearLevel()*/);
               
               plugin.getBideMap().put(name, taskID); // Should integer be task id and put damage get in player object?
               plugin.getMeleePlayerMap().get(name).setBideAmt(0);
               plugin.giveCooldown(playa, "bide", "melee", 5 /* * rpgmp.getGearLevel()*/);
               removeItem(playa);
               
               playa.sendMessage("*bide*");
            }
            else
               playa.sendMessage("You are already biding");
         }
         // Lightning spell
         else if(playa.getItemInHand().getData().toString().contains("YELLOW DYE")
               && plugin.getMagicPlayerMap().get(playa.getName()) != null){
            event.setCancelled(true);
            
            if(!plugin.getMagicPlayerMap().get(playa.getName()).isLightningReady()){
               playa.sendMessage("Lightning is still on cooldown");
               return;
            }
            
            callThor(playa);
         }
         // Confusion spell
         else if(playa.getItemInHand().getData().toString().contains("ORANGE DYE")
               && plugin.getMagicPlayerMap().get(playa.getName()) != null){
            event.setCancelled(true);
            
            if(!plugin.getMagicPlayerMap().get(playa.getName()).isConfusionReady()){
               playa.sendMessage("Confusion is still on cooldown");
               return;
            }
            
            causeConfusion(playa);
         }
         // Melancholy. Spawns sheep around mage. TODO change to make sheep provide armour
         else if(playa.getItemInHand().getType().compareTo(Material.WHEAT) == 0
               && event.getAction().toString().contains("RIGHT_CLICK")
               && plugin.getMagicPlayerMap().get(playa.getName()) != null
               && playa.getLevel() >= 13){
            event.setCancelled(true);
            RPGMagicPlayer rpgm = plugin.getMagicPlayerMap().get(playa.getName());
            
            if(!rpgm.isSheepReady()){
               playa.sendMessage("Sheep summon is still on cooldown");
               return;
            }
            else{
               //playa.getInventory().remove(Material.WHEAT);
               removeItem(playa);
               plugin.giveCooldown(playa, "baaa", "magic", 15);
               rpgm.setSheepReadiness(false);
            }
            
            Location loc = event.getPlayer().getLocation();
            
            loc.setY(loc.getY() + 1.5);
            loc.setX(loc.getX() + 1);
            rpgm.setSheepArrayID(0, loc.getWorld().spawnEntity(loc, EntityType.SHEEP).getUniqueId());
            loc.setX(loc.getX() - 2);
            rpgm.setSheepArrayID(1, loc.getWorld().spawnEntity(loc, EntityType.SHEEP).getUniqueId());
            loc.setX(loc.getX() + 1);
            loc.setZ(loc.getZ() + 1);
            rpgm.setSheepArrayID(2, loc.getWorld().spawnEntity(loc, EntityType.SHEEP).getUniqueId());
            loc.setZ(loc.getZ() - 2);
            rpgm.setSheepArrayID(3, loc.getWorld().spawnEntity(loc, EntityType.SHEEP).getUniqueId());
            
            rpgm.setSheepCount(4);
            
            if(plugin.getBuildUpMap().get(playa.getName()) == null){
               rpgm.addBuildUp(6);
               if(rpgm.getBuildUp() >= 100){
                  playa.sendMessage("Magic discharge initiated");
                  rpgm.setBuildUp(rpgm.getBuildUp() - 51);
                  plugin.getBuildUpMap().put(playa.getName(), playa.getName());
               }
            }
         }
         // Casting fireball
         else if(playa.getItemInHand().getData().toString().contains("RED DYE")
               && event.getAction().toString().contains("RIGHT_CLICK")
               && plugin.getMagicPlayerMap().get(playa.getName()) != null
               && playa.getLevel() >= 0){ //TODO changed level for testing
            event.setCancelled(true);
            String data;
            Fireball ballOfFire;
            RPGMagicPlayer rpgm = plugin.getMagicPlayerMap().get(playa.getName());
            
            if(!rpgm.isFireReady()){
               playa.sendMessage("Fireball is still on cooldown");
               return;
            }
            else{
               removeItem(playa);
               plugin.giveCooldown(playa, "fire", "magic", 5);
               rpgm.setFireReadiness(false);
            }

            rpgm.calcGearLevel(playa.getInventory());
            ballOfFire = event.getPlayer().launchProjectile(Fireball.class);
            if(plugin.getBuildUpMap().get(playa.getName()) != null)
               data = Float.toString(rpgm.getGearLevel() * 1.5F) + "|y";
            else
               data = Float.toString(rpgm.getGearLevel()) + "|n";
            
            plugin.getProjMap().put(ballOfFire, data);
            
         }
         // Active berserk mode if player has gained enough rage TODO add 'discharge' mode for mages
         else if(event.getAction().toString().contains("RIGHT_CLICK")
               && (plugin.getMeleePlayerMap().get(playa.getName()) != null
               || plugin.getMagicPlayerMap().get(playa.getName()) != null)){
            final String pName = playa.getName();
            
            // Activate berserk mode and effects
            if(plugin.getMeleePlayerMap().get(pName) != null){
               if(plugin.getMeleePlayerMap().get(pName).getRage() >= 100){
                  playa.sendMessage("RAAAAGE (Berserker mode activated)");
                  plugin.getMeleePlayerMap().get(pName).setRage(0);
                  plugin.getBerserkMap().put(pName, pName);
                  
                  /* 10 seconds of Berserker mode (increased damage, speed and
                   * sword bonuses) */
                  event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 200, 1));
                  plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                     public void run() {
                        plugin.getBerserkMap().remove(pName);
                        plugin.getServer().getPlayer(pName).sendMessage("Berserker mode deactivated");
                     }
                 }, 200L);
               }
               else
                  playa.sendMessage("Not enough rage. Current rage: " + Integer.toString(plugin.getMeleePlayerMap().get(pName).getRage()));
            }
            // Activate discharge and effects
            else if(plugin.getMagicPlayerMap().get(pName) != null){
               if(plugin.getBuildUpMap().get(pName) != null){
                  if(plugin.getMagicPlayerMap().get(pName).getBuildUp() >= 25){
                     playa.sendMessage("Magic discharge initiated");
                     plugin.getMagicPlayerMap().get(pName).setBuildUp(plugin.getMagicPlayerMap().get(pName).getBuildUp() - 25);
                     plugin.getBuildUpMap().put(pName, pName);
                  }
                  else
                     playa.sendMessage("Not enough excess magic");
               }
               else
                  playa.sendMessage("You are already set to discharge");
            }
         }
         else if(playa.getItemInHand().getType().compareTo(Material.EGG) == 0
               && plugin.getRangedPlayerMap().get(playa.getName()) != null
               && playa.getLevel() >= 7){
            if(!plugin.getRangedPlayerMap().get(playa.getName()).isEggReady())
               playa.sendMessage("Egg is still on cooldown");
            else{
               plugin.giveCooldown(playa, "egg", "ranged", 5);
               plugin.getRangedPlayerMap().get(playa.getName()).setEggReadiness(false);
            }
         }
         // Player right clicking with fist to set a quest
         else if(event.getAction().compareTo(Action.RIGHT_CLICK_BLOCK) == 0
               && event.getPlayer().getItemInHand().getType().compareTo(Material.AIR) == 0
               && plugin.isQuestCanSet()){
            plugin.getQuestStartMap().put(event.getClickedBlock().getLocation(), plugin.getQuestNumberToSet());
            plugin.setQuestCanSet(false);
            event.getPlayer().sendMessage("Quest number " + plugin.getQuestNumberToSet() + " set");
         }
         // Player right clicking with fist to unset a quest
         else if(event.getAction().compareTo(Action.RIGHT_CLICK_BLOCK) == 0
               && event.getPlayer().getItemInHand().getType().compareTo(Material.AIR) == 0
               && plugin.isQuestCanUnset()
               && plugin.getQuestStartMap().get(event.getClickedBlock().getLocation()) != null){
            plugin.getQuestStartMap().remove(event.getClickedBlock().getLocation());
            plugin.setQuestCanUnset(false);
            event.getPlayer().sendMessage("Quest block unset");
         }
         // Player just right clicking for a quest
         else if(event.getAction().compareTo(Action.RIGHT_CLICK_BLOCK) == 0
               && plugin.getQuestStartMap().get(event.getClickedBlock().getLocation()) != null){
            RPGQuest rpgq = plugin.getQuestMap().get(plugin.getQuestStartMap().get(event.getClickedBlock().getLocation()));
            RPGPlayer rpgp = null;
            
            if(plugin.getMagicPlayerMap().get(playa.getName()) != null)
               rpgp = plugin.getMagicPlayerMap().get(playa.getName());
            else if(plugin.getMeleePlayerMap().get(playa.getName()) != null)
               rpgp = plugin.getMeleePlayerMap().get(playa.getName());
            else if(plugin.getRangedPlayerMap().get(playa.getName()) != null)
               rpgp = plugin.getRangedPlayerMap().get(playa.getName());

            // Just making sure they exist with a class
            if(rpgp != null){
               // Check if the player has already completed the quest
               if(!rpgp.getComplete().contains(Integer.toString(rpgq.getQuestNumber()))){
                  // Check if the player has completed the prerequisite, if it has a prerequisite
                  if(rpgp.getComplete().contains(Integer.toString(rpgq.getPrerequisiteQuest()))
                        || rpgq.getPrerequisiteQuest() == -1){
                     boolean pass, attempted;
                     StringTokenizer st;
                     
                     pass = true;
                     attempted = false;
                     st = new StringTokenizer(rpgq.getRequiredItem(), "|");
                     
                     /* Let someone who has already made the prereqs pass
                      * regardless and set a flag to trigger win condition checks*/
                     if(!rpgp.getIncomplete().contains(Integer.toString(rpgq.getQuestNumber()))){
                        while(st.hasMoreTokens()){
                           if(!event.getPlayer().getInventory().contains(Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken())))
                              pass = false;
                        }
                     }
                     else
                        attempted = true;
                     
                     if(pass){
                        if(attempted){
                           // Win condition check
                           st = new StringTokenizer(rpgq.getFinishConditions(), "|");
                           
                           while(st.hasMoreTokens()){
                              if(!event.getPlayer().getInventory().contains(Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken())))
                                 pass = false;
                           }
                           
                           if(pass){
                              sendDialogue("pass|", event.getPlayer(), rpgq.getDialogue());
                              
                              // Set the quest as completed
                              if(rpgp.getComplete().contains("none"))
                                 rpgp.setComplete(rpgq.getQuestNumber() + "|");
                              else
                                 rpgp.setComplete(rpgp.getComplete() + rpgq.getQuestNumber() + "|");
                              
                              // Give the player the reward
                              st = new StringTokenizer(rpgq.getReward(), "|");
                              event.getPlayer().setExp((float)0.2);
                              while(st.hasMoreTokens()){
                                 String type;
                                 
                                 type = st.nextToken();
                                 if(type.compareToIgnoreCase("xp") == 0){
                                    int xp;
                                    
                                    xp = Integer.parseInt(st.nextToken());
                                    plugin.giveExp(event.getPlayer(), xp);
                                    event.getPlayer().sendMessage("You gained " + xp + " experience points!");
                                 }
                                 else{
                                    ItemStack is;
                                    
                                    is = new ItemStack(Integer.parseInt(type), Integer.parseInt(st.nextToken()));
                                    event.getPlayer().getInventory().addItem(is);
                                    event.getPlayer().sendMessage("You gained " + is.getAmount() + " " + is.getType().toString() + "!");
                                 }
                              }
                           }
                           else if(!pass)
                              sendDialogue("fail|", event.getPlayer(), rpgq.getDialogue());
                        }
                        else{
                           sendDialogue("text|", event.getPlayer(), rpgq.getDialogue());

                           // Set the quest as being active
                           if(rpgp.getIncomplete().contains("none"))
                              rpgp.setIncomplete(rpgq.getQuestNumber() + "|");
                           else if(!rpgp.getIncomplete().contains(Integer.toString(rpgq.getQuestNumber())))
                              rpgp.setIncomplete(rpgp.getIncomplete() + rpgq.getQuestNumber() + "|");
                        }
                     }
                     else
                        event.getPlayer().sendMessage("You are missing prerequisite item(s)");
                  }
                  else
                     event.getPlayer().sendMessage("You have not completed the prerequisite quest");
               }
               else
                  event.getPlayer().sendMessage("You have already completed this quest!");
            }
            else
               event.getPlayer().sendMessage("Why haven't you chosen a class yet?");
         }
      }
   }
   
   /* Return the dye or wheat to the player if they are within range of
    * interacting with a sheep. Note: This does not work the way I want since
    * giving one item causes the player to still not get the item back, and 
    * giving them two items causes the player to get two. I choose two because
    * it will reset when the player uses the ability again.*/
   @EventHandler
   public void onPlayerDyeWool(PlayerInteractEntityEvent event){
      if(event.getRightClicked() instanceof Sheep
            && plugin.getMagicPlayerMap().get(event.getPlayer().getName()) != null){
         event.setCancelled(true);
         ItemStack is = new ItemStack(351,2);
         
         if(event.getPlayer().getItemInHand().getData().toString().contains("YELLOW DYE")){
            is.setDurability((short)11);
            event.getPlayer().getInventory().setItem(0, is);
         }
         else if(event.getPlayer().getItemInHand().getData().toString().contains("RED DYE")){
            is.setDurability((short)1);
            event.getPlayer().getInventory().setItem(2, is);
         }
         else if(event.getPlayer().getItemInHand().getData().toString().contains("WHITE DYE")){
            is.setDurability((short)15);
            event.getPlayer().getInventory().setItem(1, is);
         }
         else if(event.getPlayer().getItemInHand().getData().toString().contains("MAGENTA DYE")){
            is.setDurability((short)13);
            event.getPlayer().getInventory().setItem(3, is);
         }
         else if(event.getPlayer().getItemInHand().getType().compareTo(Material.WHEAT) == 0){
            is.setType(Material.WHEAT);
            event.getPlayer().getInventory().setItem(4, is);
         }
      }
   }
   
   /* The ranged entity seeking code is borrowed from code listed by
    * DirtyStarfish on the bukkit.org forums (with modifications)*/
   private void callThor(Player playa){
      Location loc;
      Block block;
      int bx, by, bz, range;
      final float spell;
      double ex, ey, ez;
      BlockIterator bItr;
      ArrayList<LivingEntity> enemies = new ArrayList<LivingEntity>();
      RPGMagicPlayer rpgm = plugin.getMagicPlayerMap().get(playa.getName());
      
      // Base range is 10 blocks plus the casters spell ability
      rpgm.calcGearLevel(playa.getInventory());
      spell = rpgm.getGearLevel();
      range = (int)(10 + spell);
      
      for(Entity e : playa.getNearbyEntities(range, range, range)){
         if(plugin.isEnemy(e)){
            enemies.add((LivingEntity)e);
         }
      }
      
      bItr = new BlockIterator(playa.getLocation(), 0, range);
      
      while(bItr.hasNext()){
         block = bItr.next();
         bx = block.getX();
         by = block.getY();
         bz = block.getZ();
         for(LivingEntity e : enemies){
            loc = e.getLocation();
            ex = loc.getX();
            ey = loc.getY();
            ez = loc.getZ();
            
            // If entity is within the boundaries then it is the one being looked at
            if((bx - 0.75 <= ex && ex <= bx + 0.75) && (bz - 0.75 <= ez && ez <= bz + 0.75) && (by - 1 <= ey && ey <= by + 1)){
               loc.setY(loc.getY() + 1);
               loc.getWorld().strikeLightningEffect(loc);
               
               plugin.ohTheDamage(null, e, spell);
               
               // Remove item from inventory
               /*ItemStack is = new ItemStack(351,1);
               is.setDurability((short)11);
               playa.getInventory().remove(is);*/
               removeItem(playa);
               
               // Set cooldown
               plugin.giveCooldown(playa, "light", "magic", 1.5);
               rpgm.setLightningReadiness(false);
               
               if(plugin.getBuildUpMap().get(playa.getName()) == null){
                  playa.sendMessage("*Zap*");
                  rpgm.addBuildUp(6);
                  if(rpgm.getBuildUp() >= 100){
                     playa.sendMessage("Magic discharge initiated");
                     rpgm.setBuildUp(rpgm.getBuildUp() - 51);
                     plugin.getBuildUpMap().put(playa.getName(), playa.getName());
                  }
               }
               // Advanced lightning skill (area lighting). Activated on discharge
               else{
                  final ArrayList<LivingEntity> nearEnemies = new ArrayList<LivingEntity>();
                  
                  nearEnemies.add(e);
                  for(Entity victims : e.getNearbyEntities(5, 5, 5)){
                     if (plugin.isEnemy(victims)) {
                        nearEnemies.add((LivingEntity) victims);
                     }
                  }
                  
                  playa.sendMessage("*Zap zap zap*");
                  plugin.getBuildUpMap().remove(playa.getName());
                  
                  // Schedule the area lightning to be delayed
                  plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                     public void run(){
                        for(LivingEntity e : nearEnemies){
                           Location enemyLoc = e.getLocation();
                           enemyLoc.setY(enemyLoc.getY() + 1);
                           enemyLoc.getWorld().strikeLightningEffect(enemyLoc);
                           
                           plugin.ohTheDamage(null, e, spell / 2);
                        }
                     }
                 }, 20L);
               }
               return;
            }
         }
      }
   }

   private void causeConfusion(Player playa){
      Location loc;
      Block block;
      int bx, by, bz, range;
      float spell;
      double ex, ey, ez;
      BlockIterator bItr;
      ArrayList<LivingEntity> enemies = new ArrayList<LivingEntity>();
      RPGMagicPlayer rpgm = plugin.getMagicPlayerMap().get(playa.getName());
      
      // Base range is 10 blocks plus the casters spell ability
      rpgm.calcGearLevel(playa.getInventory());
      spell = rpgm.getGearLevel();
      range = (int)(10 + spell);
      
      for(Entity e : playa.getNearbyEntities(range, range, range)){
         if(plugin.isEnemy(e)){
            enemies.add((LivingEntity)e);
         }
      }
      
      bItr = new BlockIterator(playa.getLocation(), 0, range);
      
      while(bItr.hasNext()){
         block = bItr.next();
         bx = block.getX();
         by = block.getY();
         bz = block.getZ();
         for(LivingEntity e : enemies){
            loc = e.getLocation();
            ex = loc.getX();
            ey = loc.getY();
            ez = loc.getZ();
            
            // If entity is within the boundaries then it is the one being looked at
            if((bx - 0.75 <= ex && ex <= bx + 0.75) && (bz - 0.75 <= ez && ez <= bz + 0.75) && (by - 1 <= ey && ey <= by + 1)){
               final UUID eID;
               
               eID = e.getUniqueId();
               
               if(e instanceof Player)
                  ((Player) e).sendMessage("*confused*");
               
               plugin.getConfMap().put(eID, rpgm.getName());
               
               // Remove item from inventory
               /*ItemStack is = new ItemStack(351,1);
               is.setDurability((short)14);
               playa.getInventory().remove(is);*/
               removeItem(playa);
               
               // Set cooldown TODO conf could also stop pickups?
               plugin.giveCooldown(playa, "conf", "magic", 1.5);
               rpgm.setConfusionReadiness(false);
               
               if(plugin.getBuildUpMap().get(playa.getName()) == null){
                  playa.sendMessage("*Confusion*");
                  
                  // Add victim to confusion map
                  plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                     public void run(){
                        plugin.getConfMap().remove(eID);
                     }
                  }, (long) (spell) * 20L);
                  
                  rpgm.addBuildUp(6);
                  if(rpgm.getBuildUp() >= 100){
                     playa.sendMessage("Magic discharge initiated");
                     rpgm.setBuildUp(rpgm.getBuildUp() - 51);
                     plugin.getBuildUpMap().put(playa.getName(), playa.getName());
                  }
               }
               // Advanced confusion skill (longer duration). Activated on discharge
               else{
                  playa.sendMessage("*Moar confusion*");
                  plugin.getBuildUpMap().remove(playa.getName());

                  // Add victim to confusion map
                  plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                     public void run(){
                        plugin.getConfMap().remove(eID);
                     }
                 }, (long) (spell * 1.5) * 20L);
               }
               return;
            }
         }
      }
   }
   
   // Send the player the desired dialogue
   private void sendDialogue(String type, Player playa, ArrayList<String> dialogue){
      int i;
      
      for(i = 0; i < dialogue.size(); i++){
         if(dialogue.get(i).contains(type))
            playa.sendMessage(dialogue.get(i).replace(type, ""));
      }
   }
   
   private void removeItem(final Player playa){
      plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
         public void run(){
            playa.setItemInHand(null);
         }
      }, 1L);
   }
}
