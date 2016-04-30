package cz.ondraster.bettersleeping.api;

import net.minecraft.nbt.NBTTagCompound;

public class PlayerData {
   private double caffeineCounter = 0;
   private double pillCounter = 0;
   private long sleepCounter = Integer.MAX_VALUE;
   private long ticksAtLogOff = 0;
   private long dayTicksAtLogOff = 0;

   /**
    * INTERNAL VALUES! Only used for syncing to the client, do not touch these!
    */
   public long lastUpdate = Integer.MAX_VALUE;
   public int ticksSinceUpdate = 0;

   /*
    * Constructor when being loaded from NBT
    */
   public PlayerData(NBTTagCompound compound) {
      readFromNBT(compound);
   }

   /*
    * Constructor when being newly set up.
    * Param sleepCounter specifies the spawn sleep counter (usually loaded from config)
    */
   public PlayerData(long sleepCounter, int caffeineCounter) {
      this.sleepCounter = sleepCounter;
      this.caffeineCounter = caffeineCounter;
   }

   public PlayerData(long sleepCounter) {
      this(sleepCounter, 0);
   }

   public void readFromNBT(NBTTagCompound compound) {
      sleepCounter = compound.getLong("sleepCounter");
      caffeineCounter = compound.getDouble("caffeineCounter");
      pillCounter = compound.getDouble("pillCounter");
      ticksAtLogOff = compound.getLong("ticksAtLogOff");
      dayTicksAtLogOff = compound.getLong("dayTicksAtLogOff");
   }

   public void writeToNBT(NBTTagCompound compound) {
      compound.setLong("sleepCounter", sleepCounter);
      compound.setDouble("caffeineCounter", caffeineCounter);
      compound.setDouble("pillCounter", pillCounter);
      compound.setLong("ticksAtLogOff", ticksAtLogOff);
      compound.setLong("dayTicksAtLogOff", dayTicksAtLogOff);
   }

   public void increaseSleeplevel() {
      increaseSleepLevel(1);
   }

   public void increaseSleepLevel(long amount) {
      this.sleepCounter += amount;
   }

   public void decreaseSleepLevel() {
      decreaseSleepLevel(1);
   }

   public void decreaseSleepLevel(long amount) {
      this.sleepCounter -= amount;
      if (this.sleepCounter < 0)
         this.sleepCounter = 0;
   }
   
   public void setSleepLevel(long amount)
   {
	   this.sleepCounter = Math.max(amount, 0);
   }

   public void reset(long amount) {
      this.sleepCounter = amount;
      caffeineCounter = 0;
      pillCounter = 0;
      ticksAtLogOff = 0;
      dayTicksAtLogOff = 0;
   }

   public long getSleepLevel() {
      return sleepCounter;
   }

   public double getCaffeineLevel() {
      return caffeineCounter;
   }

   public double getPillLevel() {
      return pillCounter;
   }
  
   public long getTicksSinceLastLogOff()
   {
	   return ticksAtLogOff;
   }
   
   public long getDayTicksAtLastLogOff()
   {
	   return dayTicksAtLogOff;
   }
   
   public void decreaseCaffeineLevel(double amount) {
      this.caffeineCounter -= amount;
      if (caffeineCounter < 0)
         caffeineCounter = 0;
   }

   public void decreasePillLevel(double amount) {
      this.pillCounter -= amount;
      if (pillCounter < 0)
         pillCounter = 0;
   }

   public void decreaseCaffeineLevel() {
      decreaseCaffeineLevel(1);
   }

   public void decreasePillLevel() {
      decreasePillLevel(1);
   }

   public void increaseCaffeineLevel(double amount) {
      this.caffeineCounter += amount;
   }

   public void increasePillLevel(double amount) {
      pillCounter += amount;
   }

   public void increasePillLevel() {
      increaseCaffeineLevel(1);
   }
   
   public void setLoggedOff(long allTicks, long dayTicks)
   {
	   ticksAtLogOff = allTicks;
	   dayTicksAtLogOff = dayTicks;
   }
}
