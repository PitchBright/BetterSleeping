package foxie.bettersleeping;

import java.util.Random;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFood;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.event.entity.player.PlayerUseItemEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cz.ondraster.bettersleeping.api.PlayerData;
import foxie.bettersleeping.compat.CompatibilityEnviroMine;
import foxie.bettersleeping.logic.Alarm;
import foxie.bettersleeping.logic.AlternateSleep;
import foxie.bettersleeping.logic.CaffeineLogic;
import foxie.bettersleeping.logic.DebuffLogic;

public class EventHandlers {

	public static EventHandlers INSTANCE;

	private int ticksSinceUpdate = 0;

	public EventHandlers() {
		INSTANCE = this;
	}

	@SubscribeEvent
	public void onPlayerDeath(LivingDeathEvent event) {
		if (event.entity.worldObj.isRemote)
			return;

		if (!Config.enableSleepCounter)
			return;

		if (!Config.resetCounterOnDeath)
			return;

		if (event.entity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) event.entity;
			PlayerData data = BSSavedData.instance().getPlayerData(
					player.getUniqueID());
			data.reset(Config.spawnSleepCounter);
			BSSavedData.instance().markDirty();
		}
	}

	@SubscribeEvent
	public void onPreWorldTick(TickEvent.WorldTickEvent event) {
		if (!(event.world instanceof WorldServer))
			return;

		if (event.phase != TickEvent.Phase.START)
			return;
//		System.out.println(world);
//		BSLog.info("Time is %d", event.world.getWorldTime());
//		System.out.println("Time is %d", event.world.getWorldTime());
		WorldServer world = (WorldServer) event.world;

		if (world.areAllPlayersAsleep()) {
			Alarm.sleepWorld(world);
		}
	}

	@SubscribeEvent
	public void onPlayerTick(TickEvent.PlayerTickEvent event) {
		if (event.phase != TickEvent.Phase.START)
			return;

		PlayerData data = null;
		if (event.player.worldObj.isRemote)
			return;

		if (!event.player.isEntityAlive())
			return;

		data = BSSavedData.instance().getData(event.player);
		  
//		  long bedTime = data.getDayTicksAtLastLogOff() + (data.getSleepLevel() - 6000);
//		long bedTime = (((event.player.worldObj.getWorldTime() % 24000) + ((data.getSleepLevel() - 6000) % 24000)) % 24000);
//		long wakeTime = ((event.player.worldObj.getWorldTime() % 24000) + (((24000 - data.getSleepLevel()) / 3) % 24000));
//		long logTime = (data.getDayTicksAtLastLogOff() % 24000);
//		long curTime = (event.player.worldObj.getWorldTime() % 24000);
//		  BSLog.info("Curr: %d, Enr: %d, Log: %d, Bed: %d, Wake: %d", curTime, data.getSleepLevel(), logTime, bedTime, wakeTime);
		
		
		if (Config.enableSleepCounter) {
			data = BSSavedData.instance().getData(event.player);
			data.ticksSinceUpdate++;
			double ticksPerSleepCounter = Config.ticksPerSleepCounter;

			if (event.player.isSprinting())
				ticksPerSleepCounter /= Config.multiplicatorWhenSprinting;

			if (data.ticksSinceUpdate >= ticksPerSleepCounter) {
				data.ticksSinceUpdate = 0;

				if (!event.player.capabilities.isCreativeMode
						&& !event.player.isPlayerSleeping())
					data.decreaseSleepLevel();
			}

			if (event.player.isPlayerSleeping()
					&& Config.giveSleepCounterOnSleep > 0) {
				if (!(Config.capEnergyBar && data.getSleepLevel() >= Config.maximumSleepCounter))
					data.increaseSleepLevel(Config.giveSleepCounterOnSleep);
			}

			// send update about tiredness to the client
			DebuffLogic.updateClientIfNeeded(event.player, data);
		}

		if (data == null)
			return; // safety, should not happen except maybe some edge cases

		if (Config.enableDebuffs && Config.enableSleepCounter
				&& ticksSinceUpdate > 20) {
			// check for debuffs
			DebuffLogic.checkForDebuffs(event, data);

			if (Config.enableCaffeine) {
				CaffeineLogic.checkDebuff(event.player);
			}

			if (Config.enviromineSanityDecrease > 0
					&& Loader.isModLoaded("enviromine")) {
				if (Config.enviromineSanityAt > ((float) data.getSleepLevel() / Config.maximumSleepCounter) * 100f)
					CompatibilityEnviroMine.changeSanity(event.player,
							Config.enviromineSanityDecrease * (-1));
			}

			ticksSinceUpdate = 0;
		}

		BSSavedData.instance().markDirty();

		ticksSinceUpdate++;
	}

	@SubscribeEvent
	public void onPlayerSleepInBed(PlayerSleepInBedEvent event) {
		if (event.entityPlayer.worldObj.isRemote)
			return;

		if (Config.disableSleeping) {
			event.entityPlayer
					.addChatComponentMessage(new ChatComponentTranslation(
							"msg.sleepingDisabled"));
			event.result = EntityPlayer.EnumStatus.OTHER_PROBLEM;
			return;
		}

		if (Config.enableSleepCounter) {
			PlayerData data = BSSavedData.instance()
					.getData(event.entityPlayer);

			if (data.getSleepLevel() >= Config.maximumSleepCounter) {
				event.entityPlayer
						.addChatComponentMessage(new ChatComponentTranslation(
								"msg.notTired"));
				event.result = EntityPlayer.EnumStatus.OTHER_PROBLEM;
			}
		}

		// check for amount of people sleeping in this dimension
		AlternateSleep.trySleepingWorld(event.entityPlayer.worldObj);
	}

	@SubscribeEvent
	public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event)
	{
		if (event.player.worldObj == null)
			return;

		if (event.player.worldObj.isRemote)
			return;
		
		PlayerData data = BSSavedData.instance().getData(event.player);
		
		long missedTime = event.player.worldObj.getTotalWorldTime() - data.getTicksSinceLastLogOff();
		long missedTime24 = (event.player.worldObj.getWorldTime() - data.getTicksSinceLastLogOff()) % 24000;
		long energy = data.getSleepLevel();
		long curTime = (event.player.worldObj.getWorldTime() % 24000);
		long logTime = (data.getDayTicksAtLastLogOff() % 24000);
		long bedTime = ((curTime + (energy - 6000)) % 24000);
		long wakeTime = ((curTime + ((24000 - energy) / 3)) % 24000);
//		long bedTime = data.getBedTime();
//		long wakeTime = data.getWakeTime();

		data.decreaseCaffeineLevel(missedTime * Config.caffeinePerTick);
		

		BSLog.info("LOGIN - Curr: %d, Miss24: %d, Enr: %d, Log: %d, Bed: %d, Wake: %d", curTime, missedTime24, energy, logTime, bedTime, wakeTime);
		
		if (energy - missedTime24 > 6000){
			data.setSleepLevel((long) (energy - missedTime24));
//			System.out.println("Scenario 1.1 Player joins during standard energy reduction");
		}
		else
		{		
			if (energy - missedTime24 > 0){
				data.setSleepLevel((long) (6000 + ((6000 - (energy - missedTime24)) * 3)));
//				System.out.println("Scenario 2.1 Player joins during BedTime:" + (6000 - (energy - missedTime24)));
		}
			else {
			data.setSleepLevel((long) (24000 + (energy - missedTime24)));
//			System.out.println("Scenario 2.2 Player joins past WakeTime:" + (0 - (energy - missedTime24)));
			}
	   }
		missedTime24 = 0;
		
	}
	
	@SubscribeEvent
	public void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {

		PlayerData data = BSSavedData.instance().getData(event.player);
		
		long energy = data.getSleepLevel();
		long curTime = (event.player.worldObj.getWorldTime() % 24000);
		long logTime = (data.getDayTicksAtLastLogOff() % 24000);
		long bedTime = ((curTime + (energy - 6000)) % 24000);
		long wakeTime = ((curTime + ((24000 - energy) / 3)) % 24000);
//		long wakeTime = ((bedTime + 6000) % 24000);
		
		if (event.player.worldObj == null)
			return;

		if (event.player.worldObj.isRemote)
			return;
		
//		data.setLoggedOff(event.player.worldObj.getTotalWorldTime(), event.player.worldObj.getWorldTime()); // CAFFEINE NEEDS TOTAL-WORLD-TIME
		data.setLoggedOff(event.player.worldObj.getWorldTime(), event.player.worldObj.getWorldTime()); //doesn't work with Total-World-Time
		

//		  BSLog.info("LOGOUT - Curr: %d, Enr: %d, Log: %d, Bed: %d, Wake: %d", curTime, energy, logTime, bedTime, wakeTime);
		
		if (Config.percentPeopleToSleep > 1)
			return;
		
		AlternateSleep.trySleepingWorld(event.player.worldObj, true);
	}

	@SubscribeEvent
	public void onEntityJump(LivingEvent.LivingJumpEvent event) {
		if (event.entity instanceof EntityPlayer && Config.tirednessJump > 0) {
			EntityPlayer player = (EntityPlayer) event.entity;
			if (player.worldObj.isRemote)
				return;

			if (player.capabilities.isCreativeMode)
				return;

			PlayerData data = BSSavedData.instance().getData(player);
			if (player.isSprinting())
				data.decreaseSleepLevel((long) (Config.tirednessJump * Config.multiplicatorWhenSprinting));
			else
				data.decreaseSleepLevel(Config.tirednessJump);

			BSSavedData.instance().markDirty();

		}
	}

	@SubscribeEvent
	public void onPlayerWakeUpEvent(PlayerWakeUpEvent event) {
		if (event.entityPlayer.worldObj.isRemote)
			return;

		Random rand = event.entityPlayer.worldObj.rand;

		if (rand.nextFloat() < Config.chanceToGetBadNight) {
			if (rand.nextFloat() >= 0.5f)
				event.entityPlayer.addPotionEffect(new PotionEffect(
						Potion.weakness.getId(), 60, rand.nextInt(2) + 1));
			else
				event.entityPlayer.addPotionEffect(new PotionEffect(
						Potion.moveSlowdown.getId(), 60, rand.nextInt(2) + 1));
		} else if (rand.nextFloat() < Config.chanceToGetGoodNight) {
			if (rand.nextFloat() >= 0.5f)
				event.entityPlayer.addPotionEffect(new PotionEffect(Potion.heal
						.getId(), 60, rand.nextInt(2) + 1));
			else
				event.entityPlayer.addPotionEffect(new PotionEffect(
						Potion.regeneration.getId(), 60, rand.nextInt(2) + 1));
		}
	}

	@SubscribeEvent
	public void onPlayerUseItem(PlayerUseItemEvent.Finish event) {
		if (event.entityPlayer.worldObj.isRemote)
			return;

		PlayerData data = BSSavedData.instance().getData(event.entityPlayer);

		if (CaffeineLogic.isCoffee(event.item)) {
			if (event.result.getItem() instanceof ItemFood) {
				ItemFood food = (ItemFood) event.result.getItem();
				float hunger = food.func_150905_g(event.result);
				float saturation = food.func_150906_h(event.result);
				hunger *= Config.itemFoodHungerMult;
				saturation *= Config.itemFoodSaturationMult;
				data.increaseCaffeineLevel(hunger);
				data.increaseSleepLevel((int) saturation);
			} else {
				data.increaseCaffeineLevel(Config.caffeinePerItem);
				data.increaseSleepLevel(Config.tirednessPerCaffeine);
			}

			BSSavedData.instance().markDirty();
		}

		if (CaffeineLogic.isPill(event.item)) {
			data.increasePillLevel(Config.pillPerPill);
			BSSavedData.instance().markDirty();
		}

		if (CaffeineLogic.isSleepingPill(event.item)) {
			data.decreaseSleepLevel(Config.sleepingPillAmount);
			BSSavedData.instance().markDirty();
		}

		// send update about tiredness to the client
		DebuffLogic.updateClientIfNeeded(event.entityPlayer, data);
	}
}
