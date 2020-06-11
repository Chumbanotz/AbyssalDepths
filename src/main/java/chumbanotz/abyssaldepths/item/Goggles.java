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
	private static final ItemArmor.ArmorMaterial GOGGLES = EnumHelper.addArmorMaterial(AbyssalDepths.MOD_ID + "goggles", AbyssalDepths.MOD_ID + ":goggles", 0, new int[] {0, 0, 0, 0}, 0, SoundEvents.ITEM_ARMOR_EQUIP_GENERIC, 0);
	private static final PotionEffect NIGHT_VISION = new PotionEffect(MobEffects.NIGHT_VISION, Integer.MAX_VALUE, 0, true, false);
	private static final PotionEffect WATER_BREATHING = new PotionEffect(MobEffects.WATER_BREATHING, Integer.MAX_VALUE, 0, true, false);
	public Goggles() {
		super(GOGGLES, 0, EntityEquipmentSlot.HEAD);
		this.setMaxDamage(4800);
	}

	@Override
	public void onArmorTick(World world, EntityPlayer player, ItemStack stack) {
		if (player.isInsideOfMaterial(Material.WATER)) {
			player.addPotionEffect(NIGHT_VISION);
			player.addPotionEffect(WATER_BREATHING);
			if (!player.isCreative() && !player.isSpectator()) {
				stack.damageItem(1, player);
			}
		} else if (hasEffects(player)) {
			player.removePotionEffect(MobEffects.NIGHT_VISION);
			player.removePotionEffect(MobEffects.WATER_BREATHING);
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
		return AbyssalDepths.MOD_ID + ":textures/models/armor/goggles.png";
	}

	public static boolean hasEffects(EntityLivingBase entityLivingBase) {
		return hasEffect(entityLivingBase, MobEffects.NIGHT_VISION) && hasEffect(entityLivingBase, MobEffects.WATER_BREATHING);
	}

	private static boolean hasEffect(EntityLivingBase entityLivingBase, Potion potion) {
		return entityLivingBase.isPotionActive(potion) && !entityLivingBase.getActivePotionEffect(potion).doesShowParticles();
	}
}