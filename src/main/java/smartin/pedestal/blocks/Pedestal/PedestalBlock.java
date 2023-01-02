package smartin.pedestal.blocks.Pedestal;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.world.explosion.Explosion;
import org.jetbrains.annotations.Nullable;
import smartin.pedestal.ModTags;
import smartin.pedestal.Pedestal;

public class PedestalBlock extends Block implements BlockEntityProvider ,Waterloggable {

    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;

    public static final Material PedestalMaterial = (new Material.Builder(MapColor.IRON_GRAY).notSolid()).build();

    public PedestalBlock() {
        super(FabricBlockSettings.of(PedestalMaterial).strength(1.5f));
        setDefaultState(this.stateManager.getDefaultState()
                .with(Properties.HORIZONTAL_FACING, Direction.NORTH)
                .with(WATERLOGGED, false));
    }

    @SuppressWarnings("deprecation")
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        return world.getBlockState(pos.down()).getMaterial().isSolid();
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new PedestalBlockEntity(pos,state);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
        stateManager.add(Properties.HORIZONTAL_FACING, WATERLOGGED);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return (BlockState)this.getDefaultState()
                .with(Properties.HORIZONTAL_FACING, ctx.getPlayerFacing().getOpposite())
                .with(WATERLOGGED, ctx.getWorld().getFluidState(ctx.getBlockPos()).getFluid() == Fluids.WATER);
    }

    @SuppressWarnings("deprecation")
    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (state.get(WATERLOGGED)) {
            // This is for 1.17 and below: world.getFluidTickScheduler().schedule(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
            world.createAndScheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }
        if(!canPlaceAt(state, world,pos)){
            DropItems(world,pos);
            return Blocks.AIR.getDefaultState();
        }
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    private void DropItems(WorldAccess world,BlockPos pos){
        if(world.getBlockEntity(pos) instanceof PedestalBlockEntity entity && world instanceof ServerWorld serverWorld){
            world.spawnEntity(new ItemEntity(serverWorld, pos.getX(),     pos.getY(),     pos.getZ(),     entity.getWeapon().copy()));
        }
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        super.onBreak(world,pos,state,player);
        DropItems(world,pos);
    }

    @Override
    public void onDestroyedByExplosion(World world, BlockPos pos, Explosion explosion) {
        super.onDestroyedByExplosion(world, pos, explosion);
        DropItems(world,pos);
    }

    @SuppressWarnings("deprecation")
    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, ShapeContext ctx) {
        Direction dir = state.get(Properties.HORIZONTAL_FACING);
        switch(dir) {
            case NORTH:
            case SOUTH:
                return VoxelShapes.cuboid(4f/16f, 0, 5f/16f, 12f/16f, 2f/16f, 11f/16f);
            case EAST:
            case WEST:
                return VoxelShapes.cuboid(5f/16f, 0, 4f/16f, 11f/16f, 2f/16f, 12f/16f);
            default:
                return VoxelShapes.fullCube();
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
    }

    @SuppressWarnings("deprecation")
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        BlockEntity te = world.getBlockEntity(pos);
        if (!world.isClient && te instanceof PedestalBlockEntity displayTile && hand == Hand.MAIN_HAND) {
            ItemStack inHand = player.getMainHandStack();
            if (isAllowed(inHand) || inHand.isEmpty()) {
                ItemStack inPedestal = displayTile.getWeapon().copy();
                inHand = player.getMainHandStack();
                ItemStack toPedestal = inHand.copy();
                toPedestal.setCount(1);
                displayTile.setWeapon(toPedestal);
                inHand.decrement(1);
                player.giveItemStack(inPedestal);
                te.markDirty();
                world.updateListeners(pos, state, state, Block.NOTIFY_LISTENERS);
                return ActionResult.SUCCESS;
            }
        }
        return ActionResult.PASS;
    }
    private boolean isAllowed(ItemStack stack){
        if(Pedestal.SwordJsons.get("pedestal").get(Registry.ITEM.getId(stack.getItem()).toString())!=null){
            return true;
        }
        return stack.isIn(ModTags.BIG_SWORD) || stack.isIn(ModTags.NORMAL_SWORD) || stack.isIn(ModTags.SMALL_SWORD);
    }

}
