package foxie.bettersleeping.asm;

import java.util.Map;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;

@IFMLLoadingPlugin.MCVersion("1.7.10")
@IFMLLoadingPlugin.Name(BetterSleepingCoreLoader.NAME)
public class BetterSleepingCoreLoader implements IFMLLoadingPlugin {
   public static final String NAME = "BetterSleeping Core";

   @Override
   public String[] getASMTransformerClass() {
      return new String[]{PatchCollection.class.getName()};
   }

   @Override
   public String getModContainerClass() {
      return BetterSleepingCore.class.getName();
   }

   @Override
   public String getSetupClass() {
      return null;
   }

   @Override
   public void injectData(Map<String, Object> data) {

   }

   @Override
   public String getAccessTransformerClass() {
      return null;
   }
}
