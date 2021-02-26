package chumbanotz.abyssaldepths.item;

import java.util.List;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import chumbanotz.abyssaldepths.AbyssalDepths;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.util.EnumHelper;

public class Goggles extends ItemArmor {
	private static final ItemArmor.ArmorMaterial GOGGLES = EnumHelper.addArmorMaterial(AbyssalDepths.MOD_ID + ":goggles", AbyssalDepths.MOD_ID + ":goggles", 0, new int[] {0, 0, 0, 0}, 0, SoundEvents.ITEM_ARMOR_EQUIP_GENERIC, 0);
	private static final String TEXTURE = AbyssalDepths.MOD_ID + ":textures/models/armor/goggles.png";

	public Goggles() {
		super(GOGGLES, 0, EntityEquipmentSlot.HEAD);
		this.setMaxDamage(4800);
	}

	@Override
	public void onArmorTick(World world, EntityPlayer player, ItemStack stack) {
		if (world.isRemote) {
			return;
		}

		if (player.isInsideOfMaterial(Material.WATER)) {
			if (!hasEffect(player, MobEffects.NIGHT_VISION)) {
				player.removePotionEffect(MobEffects.NIGHT_VISION);
				player.addPotionEffect(new PotionEffect(MobEffects.NIGHT_VISION, Integer.MAX_VALUE, -1, true, false));
			}

			if (!hasEffect(player, MobEffects.WATER_BREATHING)) {
				player.removePotionEffect(MobEffects.WATER_BREATHING);
				player.addPotionEffect(new PotionEffect(MobEffects.WATER_BREATHING, Integer.MAX_VALUE, -1, true, false));
			}

			if (!player.capabilities.disableDamage && stack.attemptDamageItem(1, itemRand, null)) {
				player.renderBrokenItemStack(stack);
				stack.shrink(1);
			}
		} else {
			if (hasEffect(player, MobEffects.NIGHT_VISION)) {
				player.removePotionEffect(MobEffects.NIGHT_VISION);
			}

			if (hasEffect(player, MobEffects.WATER_BREATHING)) {
				player.removePotionEffect(MobEffects.WATER_BREATHING);
			}
		}
	}

	@Override
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		if (stack.isItemDamaged()) {
			int ticks = stack.getMaxDamage() - stack.getItemDamage();
			int minute = ticks / 1200;
			int second = ticks / 20 - minute * 60;
			tooltip.add(TextFormatting.GRAY + "Minutes left: " + minute + ':' + second);
		}
	}

	@Override
	public Multimap<String, AttributeModifier> getItemAttributeModifiers(EntityEquipmentSlot equipmentSlot) {
		return HashMultimap.create();
	}

	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
		return TEXTURE;
	}

	public static boolean hasEffect(EntityLivingBase entity, Potion potion) {
		PotionEffect potionEffect = entity.getActivePotionEffect(potion);
		return potionEffect != null && potionEffect.getIsAmbient() && !potionEffect.doesShowParticles() && potionEffect.getAmplifier() == -1;
	}
}