package smartin.pedestal.blocks.Pedestal;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
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
import net.minecraft.world.explosion.Explosion;
import org.jetbrains.annotations.Nullable;
import smartin.pedestal.ModTags;
import smartin.pedestal.Pedestal;

public class PedestalBlock extends Block implements BlockEntityProvider {

    public PedestalBlock() {
        super(FabricBlockSettings.of(Material.WOOD).strength(2.0f));
        setDefaultState(this.stateManager.getDefaultState().with(Properties.HORIZONTAL_FACING, Direction.NORTH));
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        super.onBreak(world,pos,state,player);
        if(world.getBlockEntity(pos) instanceof PedestalBlockEntity entity && world instanceof ServerWorld serverWorld){
            world.spawnEntity(new ItemEntity(serverWorld, pos.getX(),     pos.getY(),     pos.getZ(),     entity.getWeapon().copy()));
            world.spawnEntity(new ItemEntity(serverWorld, pos.getX(),     pos.getY(),     pos.getZ(), Registry.ITEM.get(new Identifier("pedestal:pedestal")).getDefaultStack()));
        }
    }

    @Override
    public void onDestroyedByExplosion(World world, BlockPos pos, Explosion explosion) {
        super.onDestroyedByExplosion(world, pos, explosion);
        if(world.getBlockEntity(pos) instanceof PedestalBlockEntity entity && world instanceof ServerWorld serverWorld){
            world.spawnEntity(new ItemEntity(serverWorld, pos.getX(),     pos.getY(),     pos.getZ(),     entity.getWeapon().copy()));
            world.spawnEntity(new ItemEntity(serverWorld, pos.getX(),     pos.getY(),     pos.getZ(), Registry.ITEM.get(new Identifier("pedestal:pedestal")).getDefaultStack()));
        }
    }


    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new PedestalBlockEntity(pos,state);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
        stateManager.add(Properties.HORIZONTAL_FACING);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return (BlockState)this.getDefaultState().with(Properties.HORIZONTAL_FACING, ctx.getPlayerFacing().getOpposite());
    }

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
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        BlockEntity te = world.getBlockEntity(pos);
        te.markDirty();
        if (!world.isClient && te instanceof PedestalBlockEntity displayTile) {
            if (player.isSneaking()) {
                if (player.getActiveHand() == Hand.MAIN_HAND && player.getMainHandStack().isEmpty()) {
                    final ItemStack toDrop = displayTile.getWeapon().copy();
                    displayTile.setWeapon(ItemStack.EMPTY);
                    player.giveItemStack(toDrop);
                }
            } else {
                ItemStack stack = player.getStackInHand(hand);
                boolean isSword = stack.isIn(ModTags.BIG_SWORD) || stack.isIn(ModTags.NORMAL_SWORD) || stack.isIn(ModTags.SMALL_SWORD);

                if (hand == Hand.MAIN_HAND) {
                    boolean isDisplayEmpty = displayTile.getWeapon().isEmpty();
                    if (isDisplayEmpty && isSword) {
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
        return ActionResult.SUCCESS;
    }


}
