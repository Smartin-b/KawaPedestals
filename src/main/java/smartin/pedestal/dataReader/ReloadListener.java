package smartin.pedestal.dataReader;

import com.google.gson.Gson;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.resource.SimpleResourceReloadListener;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import smartin.pedestal.Pedestal;

import java.io.BufferedReader;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class ReloadListener implements SimpleResourceReloadListener {
    @Override
    public CompletableFuture load(ResourceManager manager, Profiler profiler, Executor executor) {
        String dataFolder = "swords";
        Map<Identifier, List<Resource>> map = manager.findAllResources(dataFolder, (fileName) -> true);
        List<SwordJson> swordJsons = new ArrayList<>();
        map.forEach((identifier, resources) -> {
            if(identifier.getNamespace().equals(Pedestal.MOD_ID)){
                //Pedestal.LOGGER.info("Loading Identifier"+identifier);
                resources.forEach(resource -> {
                    try{
                        BufferedReader reader = resource.getReader();
                        swordJsons.add(new Gson().fromJson(reader, SwordJson.class));
                    }
                    catch (Exception e){
                        Pedestal.LOGGER.warn("Error Loading Resource"+identifier+""+resources.toString());
                    }
                });
            }
        });
        return CompletableFuture.completedFuture(swordJsons);
    }

    @Override
    public CompletableFuture<Void> apply(Object data, ResourceManager manager, Profiler profiler, Executor executor) {
        return CompletableFuture.runAsync(() -> {
            List<SwordJson> list = (List<SwordJson>) data;
            Map<String,SwordJson.TranslationObject> pedestal= new HashMap<>();
            Map<String,SwordJson.TranslationObject> hanger = new HashMap<>();
            list.forEach(swordJson -> {
                if(swordJson.hanger!=null){
                    hanger.put(swordJson.id, swordJson.hanger);
                }
                if(swordJson.pedestal!=null){
                    pedestal.put(swordJson.id, swordJson.pedestal);
                }
            });
            Pedestal.SwordJsons.put("pedestal",pedestal);
            Pedestal.SwordJsons.put("hanger",hanger);
            if(Pedestal.server!=null){
                for (ServerPlayerEntity player : PlayerLookup.all(Pedestal.server)) {
                    Pedestal.SendToPlayer(player);
                }
            }
        });
    }

    @Override
    public Identifier getFabricId() {
        return new Identifier(Pedestal.MOD_ID,"listener");
    }
}
