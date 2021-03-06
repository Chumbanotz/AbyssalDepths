package chumbanotz.abyssaldepths;

import chumbanotz.abyssaldepths.item.ADItems;
import chumbanotz.abyssaldepths.item.Goggles;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = AbyssalDepths.MOD_ID)
public class EventHandler {
	@SubscribeEvent
	public static void onEquipmentChange(LivingEquipmentChangeEvent event) {
		if (event.getEntityLiving() instanceof EntityPlayer && event.getFrom().getItem() == ADItems.GOGGLES && event.getSlot() == EntityEquipmentSlot.HEAD) {
			if (Goggles.hasEffect(event.getEntityLiving(), MobEffects.NIGHT_VISION)) {
				event.getEntityLiving().removePotionEffect(MobEffects.NIGHT_VISION);
			}

			if (Goggles.hasEffect(event.getEntityLiving(), MobEffects.WATER_BREATHING)) {
				event.getEntityLiving().removePotionEffect(MobEffects.WATER_BREATHING);
			}
		}
	}

	@SubscribeEvent
	public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
		if (event.getModID().equals(AbyssalDepths.MOD_ID)) {
			ConfigManager.sync(AbyssalDepths.MOD_ID, Config.Type.INSTANCE);
		}
	}
}