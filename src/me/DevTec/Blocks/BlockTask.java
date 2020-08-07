package me.DevTec.Blocks;

import java.util.List;

import me.DevTec.Other.Position;
import me.DevTec.Other.TheMaterial;

public interface BlockTask {
	public long set(Position block, TheMaterial toSet);
	
	public long set(Position block, TheMaterial toSet, TheMaterial ignore);
	
	public long set(Position block, TheMaterial toSet, List<TheMaterial> ignore);
	
	public TheMaterial get(Position block);
	
	public long replace(Position block, TheMaterial toReplace, TheMaterial toSet);
	
	public long replace(Position block, List<TheMaterial> toReplace, TheMaterial toSet);
}
