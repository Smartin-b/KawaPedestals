package smartin.pedestal.blocks.WallHanger;

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
import net.minecraft.util.Identifier;
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

public class WallHanger extends Block implements BlockEntityProvider,Waterloggable {

    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;

    public static final Material WallHangerMaterial = (new Material.Builder(MapColor.BROWN).notSolid()).build();

    public WallHanger() {
        super(FabricBlockSettings.of(WallHangerMaterial).strength(2.0f));
        setDefaultState(this.stateManager.getDefaultState()
                .with(Properties.HORIZONTAL_FACING, Direction.NORTH)
                .with(WATERLOGGED, false));
    }

    @SuppressWarnings("deprecation")
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        BlockState placingOn = world.getBlockState(pos.offset(state.get(Properties.HORIZONTAL_FACING).getOpposite()));
        return placingOn.getMaterial().isSolid();
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

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return (BlockState)this.getDefaultState()
                .with(Properties.HORIZONTAL_FACING, ctx.getSide())
                .with(WATERLOGGED, ctx.getWorld().getFluidState(ctx.getBlockPos()).getFluid() == Fluids.WATER);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new WallHangerEntity(pos,state);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
        stateManager.add(Properties.HORIZONTAL_FACING, WATERLOGGED);
    }

    @SuppressWarnings("deprecation")
    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, ShapeContext ctx) {
        Direction dir = state.get(Properties.HORIZONTAL_FACING);
        switch(dir) {
            case WEST:
                return VoxelShapes.cuboid(14f/16f, 9f/16f, 3f/16f, 16f/16f, 14f/16f, 13f/16f);
            case EAST:
                return VoxelShapes.cuboid(0f/16f, 9f/16f, 3f/16f, 2f/16f, 14f/16f, 13f/16f);
            case SOUTH:
                return VoxelShapes.cuboid(3f/16f, 9f/16f, 0f/16f, 13f/16f, 14f/16f, 2f/16f);
            case NORTH:
                return VoxelShapes.cuboid(3f/16f, 9f/16f, 14f/16f, 13f/16f, 14f/16f, 16f/16f);
            default:
                return VoxelShapes.fullCube();
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
    public FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
    }

    @SuppressWarnings("deprecation")
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        BlockEntity te = world.getBlockEntity(pos);
        te.markDirty();
        if (!world.isClient && te instanceof WallHangerEntity displayTile) {
            if (player.isSneaking()) {
                if (player.getActiveHand() == Hand.MAIN_HAND && player.getMainHandStack().isEmpty()) {
                    final ItemStack toDrop = displayTile.getWeapon().copy();
                    displayTile.setWeapon(ItemStack.EMPTY);
                    player.giveItemStack(toDrop);
                }
            } else {
                ItemStack stack = player.getStackInHand(hand);

                if (hand == Hand.MAIN_HAND) {
                    boolean isDisplayEmpty = displayTile.getWeapon().isEmpty();
                    if (isDisplayEmpty) {
                        ItemStack copy = stack.copy();
                        displayTile.setWeapon(copy);
                        stack.decrement(1);
                        world.updateListeners(pos, state, state, Block.NOTIFY_LISTENERS);
                        return ActionResult.SUCCESS;
                    }
                    if (!isDisplayEmpty && stack.isEmpty()) {
                        world.updateListeners(pos, state, state, Block.NOTIFY_LISTENERS);
                    }
                }
            }
        }
        world.updateListeners(pos, state, state, Block.NOTIFY_LISTENERS);
        return ActionResult.PASS;
    }

    private void DropItems(WorldAccess world,BlockPos pos){
        if(world.getBlockEntity(pos) instanceof WallHangerEntity entity && world instanceof ServerWorld serverWorld){
            world.spawnEntity(new ItemEntity(serverWorld, pos.getX(),     pos.getY(),     pos.getZ(),     entity.getWeapon().copy()));
            world.spawnEntity(new ItemEntity(serverWorld, pos.getX(),     pos.getY(),     pos.getZ(), Registry.ITEM.get(new Identifier("pedestal:wall_hanger")).getDefaultStack()));
        }
    }


}
