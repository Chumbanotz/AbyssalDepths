package chumbanotz.abyssaldepths;

import java.util.List;

import chumbanotz.abyssaldepths.block.ADBlocks;
import chumbanotz.abyssaldepths.block.Seaweed;
import chumbanotz.abyssaldepths.entity.SeaSerpent;
import chumbanotz.abyssaldepths.entity.Seahorse;
import chumbanotz.abyssaldepths.entity.billfish.Sailfish;
import chumbanotz.abyssaldepths.entity.billfish.Swordfish;
import chumbanotz.abyssaldepths.entity.fish.Basslet;
import chumbanotz.abyssaldepths.entity.fish.Butterflyfish;
import chumbanotz.abyssaldepths.entity.fish.Clownfish;
import chumbanotz.abyssaldepths.entity.fish.CommonFish;
import chumbanotz.abyssaldepths.item.Goggles;
import chumbanotz.abyssaldepths.item.SpikeSword;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemFood;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistryEntry;

@Mod.EventBusSubscriber(modid = AbyssalDepths.MOD_ID)
public class RegistryHandler {
	private static int entityId;

	@SubscribeEvent
	public static void onBlockRegistry(RegistryEvent.Register<Block> event) {
		event.getRegistry().register(setRegistryName("seaweed", new Seaweed()));
	}

	@SubscribeEvent
	public static void onItemRegistry(RegistryEvent.Register<Item> event) {
		event.getRegistry().registerAll(
				setRegistryName("seaweed", new ItemBlock(ADBlocks.SEAWEED)),
				setRegistryName("goggles", new Goggles()),
				setRegistryName("short_billfish_spike", new Item()),
				setRegistryName("long_billfish_spike", new Item()),
				setRegistryName("spike_sword", new SpikeSword()),
				setRegistryName("bannerfish", new ItemFood(3, 0.4F, false)),
				setRegistryName("butterflyfish", new ItemFood(2, 0.1F, false)),
				setRegistryName("cooked_butterflyfish", new ItemFood(6, 0.6F, false)),
				setRegistryName("masked_butterflyfish", new ItemFood(2, 0.1F, false)),
				setRegistryName("raccoon_butterflyfish", new ItemFood(2, 0.1F, false)),
				setRegistryName("spotfin_butterflyfish", new ItemFood(2, 0.1F, false))
				);
	}

	@SubscribeEvent
	public static void onEntityEntryRegistry(RegistryEvent.Register<EntityEntry> event) {
		event.getRegistry().registerAll(
				createEntityEntry("fish", CommonFish.class, 8762546, 12307920).build(),
				createEntityEntry("clownfish", Clownfish.class, 13785366, 16777215).build(),
				createEntityEntry("seahorse", Seahorse.class, 13818758, 7833651).build(),
				createEntityEntry("butterflyfish", Butterflyfish.class, 8762546, 12307920).build(),
				createEntityEntry("masked_butterflyfish", Butterflyfish.Masked.class, 16438016, 3817060).build(),
				createEntityEntry("raccoon_butterflyfish", Butterflyfish.Raccoon.class, 16297728, 1513499).build(),
				createEntityEntry("spotfin_butterflyfish", Butterflyfish.Spotfin.class, 16311296, 16121335).build(),
				createEntityEntry("bannerfish", Butterflyfish.Banner.class, 16183797, 1184274).build(),
				createEntityEntry("fairy_basslet", Basslet.Fairy.class, 12599507, 16693761).build(),
				createEntityEntry("blackcap_basslet", Basslet.BlackCap.class, 12599507, 1184274).build(),
				createEntityEntry("sailfish", Sailfish.class, 1060456, 14737632).build(),
				createEntityEntry("swordfish", Swordfish.class, 1060456, 14737632).build(),
				createEntityEntry("sea_serpent", SeaSerpent.class, 5938242, 9398119).build()
				);

		addSpawns();
	}

	private static <T extends EntityLiving> EntityEntryBuilder<?> createEntityEntry(String name, Class<T> entityClass, int eggPrimary, int eggSecondary) {
		return createEntityEntry(name, entityClass).egg(eggPrimary, eggSecondary).tracker(80, 3, true);
	}

	private static <T extends Entity> EntityEntryBuilder<T> createEntityEntry(String name, Class<T> entityClass) {
		if (EntityLiving.class.isAssignableFrom(entityClass)) {
			LootTableList.register(AbyssalDepths.prefix("entities/" + name));
			EntitySpawnPlacementRegistry.setPlacementType(entityClass, EntityLiving.SpawnPlacementType.IN_WATER);
		}

		return EntityEntryBuilder.<T>create().entity(entityClass).id(AbyssalDepths.prefix(name), entityId++).name(AbyssalDepths.MOD_ID + '.' + name);
	}

	private static void addSpawns() {
		if (ADConfig.SPAWNS.biomeWhitelist.length == 0) {
			return;
		}

		for (Biome biome : ForgeRegistries.BIOMES) {
			for (String modID : ADConfig.SPAWNS.biomeWhitelist) {
				if (biome.getRegistryName() == null || !biome.getRegistryName().getNamespace().equals(modID)) {
					continue;
				}
			}

			List<Biome.SpawnListEntry> waterEntries = biome.getSpawnableList(EnumCreatureType.WATER_CREATURE);
			if (waterEntries.isEmpty()) {
				continue;
			}

			if (BiomeDictionary.hasType(biome, BiomeDictionary.Type.RIVER)) {
				addSpawn(waterEntries, CommonFish.class, ADConfig.SPAWNS.fishWeight, 3, 5);
			}

			if (BiomeDictionary.hasType(biome, BiomeDictionary.Type.OCEAN)) {
				addSpawn(waterEntries, CommonFish.class, ADConfig.SPAWNS.fishWeight, 3, 5);
				addSpawn(waterEntries, Clownfish.class, ADConfig.SPAWNS.clownfishWeight, 2, 3);
				addSpawn(waterEntries, Seahorse.class, ADConfig.SPAWNS.seahorseWeight, 1, 3);
				addSpawn(waterEntries, Butterflyfish.class, ADConfig.SPAWNS.butterflyfishWeight, 2, 5);
				addSpawn(waterEntries, Butterflyfish.Masked.class, ADConfig.SPAWNS.maskedButterflyfishWeight, 2, 5);
				addSpawn(waterEntries, Butterflyfish.Raccoon.class, ADConfig.SPAWNS.raccoonButterflyfishWeight, 3, 5);
				addSpawn(waterEntries, Butterflyfish.Spotfin.class, ADConfig.SPAWNS.spotfinButterflyfishWeight, 3, 5);
				addSpawn(waterEntries, Butterflyfish.Banner.class, ADConfig.SPAWNS.bannerfishWeight, 2, 5);
				addSpawn(waterEntries, Basslet.Fairy.class, ADConfig.SPAWNS.fairyBassletWeight, 1, 4);
				addSpawn(waterEntries, Basslet.BlackCap.class, ADConfig.SPAWNS.blackcapBassletWeight, 1, 4);
				addSpawn(waterEntries, Sailfish.class, ADConfig.SPAWNS.sailfishWeight, 1, 5);
				addSpawn(waterEntries, Swordfish.class, ADConfig.SPAWNS.swordfishWeight, 1, 4);
				addSpawn(waterEntries, SeaSerpent.class, ADConfig.SPAWNS.seaSerpentWeight, 1, 2);
			}			
		}
	}

	private static void addSpawn(List<Biome.SpawnListEntry> entries, Class<? extends EntityLiving> entityClass, int weight, int min, int max) {
		if (weight > 0) {
			entries.add(new Biome.SpawnListEntry(entityClass, weight, min, max));
		}
	}

	@SubscribeEvent
	public static void remapEntityEntries(RegistryEvent.MissingMappings<EntityEntry> event) {
		for (RegistryEvent.MissingMappings.Mapping<EntityEntry> mapping : event.getAllMappings()) {
			if (mapping.key.getPath().equals("body_part")) {
				mapping.ignore();
			}
		}
	}

	public static <T extends IForgeRegistryEntry<T>> T setRegistryName(String name, T entry) {
		ResourceLocation registryName = AbyssalDepths.prefix(name);
		if (entry instanceof Block) {
			((Block)entry).setTranslationKey(registryName.getNamespace() + '.' + registryName.getPath());
			((Block)entry).setCreativeTab(AbyssalDepths.CREATIVE_TAB);
		}

		if (entry instanceof Item) {
			((Item)entry).setTranslationKey(registryName.getNamespace() + '.' + registryName.getPath());
			((Item)entry).setCreativeTab(AbyssalDepths.CREATIVE_TAB);
		}

		return entry.setRegistryName(registryName);
	}
}