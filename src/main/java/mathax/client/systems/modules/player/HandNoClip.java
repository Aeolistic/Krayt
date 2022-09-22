package mathax.client.systems.modules.player;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import mathax.client.eventbus.EventHandler;
import mathax.client.events.world.TickEvent;
import mathax.client.systems.modules.Categories;
import mathax.client.systems.modules.Module;
import mathax.client.utils.Utils;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.Set;

@SearchTags({"hand noclip", "hand no clip"})
public final class HandNoClipHack extends modules
{
	private final BlockListSetting blocks = new BlockListSetting("Blocks",
		"The blocks you want to reach through walls.", "minecraft:barrel",
		"minecraft:black_shulker_box", "minecraft:blue_shulker_box",
		"minecraft:brown_shulker_box", "minecraft:chest",
		"minecraft:cyan_shulker_box", "minecraft:dispenser",
		"minecraft:dropper", "minecraft:ender_chest",
		"minecraft:gray_shulker_box", "minecraft:green_shulker_box",
		"minecraft:hopper", "minecraft:light_blue_shulker_box",
		"minecraft:light_gray_shulker_box", "minecraft:lime_shulker_box",
		"minecraft:magenta_shulker_box", "minecraft:orange_shulker_box",
		"minecraft:pink_shulker_box", "minecraft:purple_shulker_box",
		"minecraft:red_shulker_box", "minecraft:shulker_box",
		"minecraft:trapped_chest", "minecraft:white_shulker_box",
		"minecraft:yellow_shulker_box");
	
	public HandNoClipHack()
	{
		super("HandNoClip");
		
		setCategory(Category.BLOCKS);
		addSetting(blocks);
	}
	
	public boolean isBlockInList(BlockPos pos)
	{
		return blocks.getBlockNames().contains(BlockUtils.getName(pos));
	}
	
	// See AbstractBlockStateMixin.onGetOutlineShape()
}
