package mathax.client.mixin

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.MapCodec;

import net.minecraft.block.AbstractBlock.AbstractBlockState;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.fluid.FluidState;
import net.minecraft.state.State;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import mathax.client.MatHax;
import mathax.client.events.world.AmbientOcclusionEvent;
import mathax.client.events.world.CollisionShapeEvent;
import mathax.client.systems.modules.player.HandNoClip;

@Mixin(AbstractBlockState.class)
public abstract class AbstractBlockStateMixin extends State<Block, BlockState>
{
	private AbstractBlockStateMixin(WurstClient wurst, Block object,
		ImmutableMap<Property<?>, Comparable<?>> immutableMap,
		MapCodec<BlockState> mapCodec)
	{
		super(object, immutableMap, mapCodec);
	}
	
	@Inject(at = @At("TAIL"),
		method = {
			"isFullCube(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;)Z"},
		cancellable = true)
	private void onIsFullCube(BlockView world, BlockPos pos,
		CallbackInfoReturnable<Boolean> cir)
	{
		IsNormalCubeEvent event = new IsNormalCubeEvent();
		EventManager.fire(event);
		
		cir.setReturnValue(cir.getReturnValue() && !event.isCancelled());
	}
	
	@Inject(at = @At("TAIL"),
		method = {
			"getAmbientOcclusionLightLevel(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;)F"},
		cancellable = true)
	private void onGetAmbientOcclusionLightLevel(BlockView blockView,
		BlockPos blockPos, CallbackInfoReturnable<Float> cir)
	{
		GetAmbientOcclusionLightLevelEvent event =
			new GetAmbientOcclusionLightLevelEvent((BlockState)(Object)this,
				cir.getReturnValueF());
		
		EventManager.fire(event);
		cir.setReturnValue(event.getLightLevel());
	}
	
	@Inject(at = @At("HEAD"),
		method = {
			"getOutlineShape(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/ShapeContext;)Lnet/minecraft/util/shape/VoxelShape;"},
		cancellable = true)
	private void onGetOutlineShape(BlockView view, BlockPos pos,
		ShapeContext context, CallbackInfoReturnable<VoxelShape> cir)
	{
		if(context == ShapeContext.absent())
			return;
		
		HackList hax = WurstClient.INSTANCE.getHax();
		if(hax == null)
			return;
		
		HandNoClipHack handNoClipHack = hax.handNoClipHack;
		if(!handNoClipHack.isEnabled() || handNoClipHack.isBlockInList(pos))
			return;
		
		cir.setReturnValue(VoxelShapes.empty());
	}
	
	@Inject(at = @At("HEAD"),
		method = "getCollisionShape(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/ShapeContext;)Lnet/minecraft/util/shape/VoxelShape;",
		cancellable = true)
	private void onGetCollisionShape(BlockView world, BlockPos pos,
		ShapeContext context, CallbackInfoReturnable<VoxelShape> cir)
	{
		if(getFluidState().isEmpty())
			return;
		
		HackList hax = WurstClient.INSTANCE.getHax();
		if(hax == null || !hax.jesusHack.shouldBeSolid())
			return;
		
		cir.setReturnValue(VoxelShapes.fullCube());
		cir.cancel();
	}
	
	@Shadow
	public abstract FluidState getFluidState();
}
