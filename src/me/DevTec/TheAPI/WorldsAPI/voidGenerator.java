package me.DevTec.TheAPI.WorldsAPI;

import java.util.Random;

import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.generator.ChunkGenerator;

import me.DevTec.TheAPI.TheAPI;

public class voidGenerator extends ChunkGenerator {
	private static String the_void = TheAPI.isNewVersion()?"THE_VOID":"VOID";

	@SuppressWarnings("deprecation")
	@Override
	public ChunkGenerator.ChunkData generateChunkData(World world, Random random, int chunkx, int chunkz,
			ChunkGenerator.BiomeGrid biome) {
		ChunkGenerator.ChunkData data = this.createChunkData(world);
		for (int x = 0; x < 16; ++x) {
			for (int z = 0; z < 16; ++z) {
				biome.setBiome(x, z, Biome.valueOf(the_void));
			}
		}
		return data;
	}
}
