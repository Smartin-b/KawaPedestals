package smartin.pedestal;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.*;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.resource.ResourceType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import smartin.pedestal.blocks.ModBlocks;
import smartin.pedestal.dataReader.ReloadListener;
import smartin.pedestal.dataReader.SwordJson;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class Pedestal implements ModInitializer {

    public static final String MOD_ID = "pedestal";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
    public static final Identifier DATAPACKET = new Identifier(MOD_ID,"datapacket");
    public static Map<String, Map<String, SwordJson.TranslationObject>> SwordJsons = new HashMap<>();
    public static MinecraftServer server;

    @Override
    public void onInitialize() {
        ModBlocks.registerBlocks();
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new ReloadListener());
        ServerPlayConnectionEvents.JOIN.register(this::onJoin);
        ServerLifecycleEvents.SERVER_STARTING.register(this::onServerStart);
        ClientPlayNetworking.registerGlobalReceiver(Pedestal.DATAPACKET, (client, handler, buf, responseSender) -> {
            final String configjson = buf.readString();
            client.execute(() -> {
                System.out.println("recieving"+configjson);
                Gson gson = new Gson();
                Type type = new TypeToken<Map<String, Map<String, SwordJson.TranslationObject>>>(){}.getType();
                SwordJsons = gson.fromJson(configjson,type);
                System.out.println(SwordJsons.size());
            });
        });
    }

    private void onServerStart(MinecraftServer minecraftServer) {
        this.server = minecraftServer;
    }

    private void onJoin(ServerPlayNetworkHandler serverPlayNetworkHandler, PacketSender packetSender, MinecraftServer minecraftServer) {
        SendToPlayer(serverPlayNetworkHandler.player);
    }
    public static void SendToPlayer(ServerPlayerEntity player){
        PacketByteBuf buf = PacketByteBufs.create();
        Gson gson = new Gson();
        buf.writeString(gson.toJson(SwordJsons));
        System.out.println("sending"+gson.toJson(SwordJsons));
        ServerPlayNetworking.send(player, Pedestal.DATAPACKET, buf);
    }
}
