package smartin.pedestal.blocks.WallHanger;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import smartin.pedestal.blocks.ModBlocks;

public class WallHangerEntity extends BlockEntity {

    private ItemStack weapon;

    public WallHangerEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.WALL_HANGER_BLOCK_ENTITY, pos, state);
        this.weapon = ItemStack.EMPTY;
        setWeapon(weapon);
    }
    public ItemStack getWeapon(){
        return weapon;
    }
    public void setWeapon(ItemStack itemStack){
        weapon = itemStack;
        this.toUpdatePacket();
    }
    // Serialize the BlockEntity
    @Override
    public void writeNbt(NbtCompound nbt) {
        // Save the current value of the number to the nbt
        if(weapon!=null)
            nbt.put("displayed_item", weapon.writeNbt(new NbtCompound()));
        super.writeNbt(nbt);
    }
    // Deserialize the BlockEntity
    @Override
    public void readNbt(NbtCompound nbt) {
        if (nbt.contains("displayed_item")) {
            this.weapon = ItemStack.fromNbt(nbt.getCompound("displayed_item"));
        }
        super.readNbt(nbt);
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }
}
