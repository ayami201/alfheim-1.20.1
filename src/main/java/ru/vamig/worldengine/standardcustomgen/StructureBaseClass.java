package ru.vamig.worldengine.standardcustomgen;

// PORT: импорты 1.20.1 (World, Random 1.7.10 — Level, RandomSource); генератор чанков WorldEngine (WE_ChunkProvider) — КТ-6
import net.minecraft.world.level.Level;
//import ru.vamig.worldengine.WE_ChunkProvider;

import net.minecraft.util.RandomSource;

public abstract class StructureBaseClass {
	
	// PORT: генератор чанков WorldEngine (КТ-6) — Object: постройки из саженцев передают null
	public abstract boolean generate(Level world, RandomSource rand, int x, int y, int z, Object chunkProvider);
//	public abstract boolean generate(World world, Random rand, int x, int y, int z, WE_ChunkProvider chunkProvider);
}
