package chumbanotz.abyssaldepths;

import net.minecraftforge.common.config.Config;

@Config(modid = AbyssalDepths.MOD_ID, category = "")
public class ADConfig {
	@Config.LangKey("config.abyssaldepths.generation")
	public static final Generation GENERATION = new Generation();

	@Config.LangKey("config.abyssaldepths.spawns")
	public static final Spawns SPAWNS = new Spawns();

	public static class Generation {
		@Config.LangKey("config.abyssaldepths.generation.generate_rocks")
		@Config.RequiresMcRestart
		public boolean generateRocks = true;

		@Config.LangKey("config.abyssaldepths.generation.generate_seaweed")
		@Config.RequiresMcRestart
		public boolean generateSeaweed = true;
	}

	public static class Spawns {
		@Config.LangKey("config.abyssaldepths.spawns.biomeWhitelist")
		@Config.Comment({"The mod IDs of the biomes that these mobs are allowed to spawn in, only vanilla biomes by default", "Example - 'minecraft, twilightforest'", "You can see a mod's ID by clicking on the 'Mods' button in-game and looking on the right"})
		public String[] biomeWhitelist = {"minecraft"};

		@Config.LangKey("entity.abyssaldepths.bannerfish.name")
		@Config.RangeInt(min = 0, max = 100)
		@Config.RequiresMcRestart
		public int bannerfishWeight = 7;

		@Config.LangKey("entity.abyssaldepths.blackcap_basslet.name")
		@Config.RangeInt(min = 0, max = 100)
		@Config.RequiresMcRestart
		public int blackcapBassletWeight = 7;

		@Config.LangKey("entity.abyssaldepths.butterflyfish.name")
		@Config.RangeInt(min = 0, max = 100)
		@Config.RequiresMcRestart
		public int butterflyfishWeight = 9;

		@Config.LangKey("entity.abyssaldepths.clownfish.name")
		@Config.RangeInt(min = 0, max = 100)
		@Config.RequiresMcRestart
		public int clownfishWeight = 7;

		@Config.LangKey("entity.abyssaldepths.fairy_basslet.name")
		@Config.RangeInt(min = 0, max = 100)
		@Config.RequiresMcRestart
		public int fairyBassletWeight = 7;

		@Config.LangKey("entity.abyssaldepths.fish.name")
		@Config.RangeInt(min = 0, max = 100)
		@Config.RequiresMcRestart
		public int fishWeight = 10;

		@Config.LangKey("entity.abyssaldepths.masked_butterflyfish.name")
		@Config.RangeInt(min = 0, max = 100)
		@Config.RequiresMcRestart
		public int maskedButterflyfishWeight = 7;

		@Config.LangKey("entity.abyssaldepths.raccoon_butterflyfish.name")
		@Config.RangeInt(min = 0, max = 100)
		@Config.RequiresMcRestart
		public int raccoonButterflyfishWeight = 7;

		@Config.LangKey("entity.abyssaldepths.sailfish.name")
		@Config.RangeInt(min = 0, max = 100)
		@Config.RequiresMcRestart
		public int sailfishWeight = 1;

		@Config.LangKey("entity.abyssaldepths.seahorse.name")
		@Config.RangeInt(min = 0, max = 100)
		@Config.RequiresMcRestart
		public int seahorseWeight = 4;

		@Config.LangKey("entity.abyssaldepths.sea_serpent.name")
		@Config.RangeInt(min = 0, max = 100)
		@Config.RequiresMcRestart
		public int seaSerpentWeight = 1;

		@Config.LangKey("entity.abyssaldepths.spotfin_butterflyfish.name")
		@Config.RangeInt(min = 0, max = 100)
		@Config.RequiresMcRestart
		public int spotfinButterflyfishWeight = 7;

		@Config.LangKey("entity.abyssaldepths.swordfish.name")
		@Config.RangeInt(min = 0, max = 100)
		@Config.RequiresMcRestart
		public int swordfishWeight = 1;
	}
}