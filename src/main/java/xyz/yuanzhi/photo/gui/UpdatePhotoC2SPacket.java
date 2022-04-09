package xyz.yuanzhi.photo.gui;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.NetworkThreadUtils;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import xyz.yuanzhi.photo.block.PhotoEntity;

public class UpdatePhotoC2SPacket implements Packet<ServerPlayPacketListener> {
    private final BlockPos pos;
    private final String url;
    private final float[] params;
    private final int positionType;

    public UpdatePhotoC2SPacket(BlockPos pos, String url, float width, float height, float x,
                                float y, float z, int positionType) {
        this.pos = pos;
        this.url = url;
        this.params = new float[]{width, height, x, y, z};
        this.positionType = positionType;
    }

    public UpdatePhotoC2SPacket(PacketByteBuf buf) {
        this.pos = buf.readBlockPos();
        this.url = buf.readString();
        this.params = new float[5];

        for (int i = 0; i < 5; i++) {
            this.params[i] = buf.readFloat();
        }

        this.positionType = buf.readInt();
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeBlockPos(this.pos);
        buf.writeString(this.url);
        for (int i = 0; i < 5; i++) {
            buf.writeFloat(this.params[i]);
        }
        buf.writeInt(this.positionType);
    }

    @Override
    public void apply(ServerPlayPacketListener listener) {
        if (listener instanceof  ServerPlayNetworkHandler handler) {
            NetworkThreadUtils.forceMainThread(this, listener, handler.player.getWorld());
            ServerWorld world = handler.player.getWorld();
            BlockPos blockPos = this.pos;

            if (world.isChunkLoaded(blockPos)) {
                BlockState blockState = world.getBlockState(blockPos);
                BlockEntity blockEntity = world.getBlockEntity(blockPos);
                if (!(blockEntity instanceof PhotoEntity photoEntity)) {
                    return;
                }

                photoEntity.setUrl(url);
                photoEntity.setWidth(params[0]);
                photoEntity.setHeight(params[1]);
                photoEntity.setXOffset(params[2]);
                photoEntity.setYOffset(params[3]);
                photoEntity.setZOffset(params[4]);
                photoEntity.setPositionType(positionType);
                photoEntity.markDirty();
                world.updateListeners(blockPos, blockState, blockState, 3);
            }
        }
    }
}
