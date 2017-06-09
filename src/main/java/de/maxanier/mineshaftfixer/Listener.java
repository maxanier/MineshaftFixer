package de.maxanier.mineshaftfixer;

import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderOverworld;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraft.world.gen.structure.MapGenStructureData;
import net.minecraft.world.gen.structure.StructureStart;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.lang.reflect.Field;
import java.util.Set;

/**
 * MineshaftFixer
 *
 * @author maxanier
 */
public class Listener {

    private final Set<Integer> disabled = Sets.newHashSet();

    public Listener() {
        disabled.add(-1);
        disabled.add(1);
    }

    @SubscribeEvent
    public void onPreWorldTick(TickEvent.WorldTickEvent event) {
        if (event.side == Side.SERVER && event.phase == TickEvent.Phase.START) {
            if (event.world instanceof WorldServer) {
                if (disabled.contains(event.world.provider.getDimension())) {
                    return;
                }
                if (event.world.getTotalWorldTime() % 1024 == 0) {
                    boolean failed = false;
                    IChunkProvider provider = event.world.getChunkProvider();
                    if (provider instanceof ChunkProviderServer) {
                        IChunkGenerator generator = ((ChunkProviderServer) provider).chunkGenerator;
                        if (generator instanceof ChunkProviderOverworld) {
                            clean(event.world.provider.getDimension(), (ChunkProviderOverworld) generator);
                        } else if ("org.spongepowered.mod.world.gen.SpongeChunkGeneratorForge".equals(generator.getClass().getName())) {
                            failed = true;
                            try {
                                Class spongeGeneratorClass = Class.forName("org.spongepowered.common.world.gen.SpongeChunkGenerator");
                                if (!spongeGeneratorClass.isInstance(generator)) {
                                    warn("Something is wrong about %s", generator);
                                }
                                Field f = spongeGeneratorClass.getDeclaredField("baseGenerator");
                                f.setAccessible(true);
                                Object generator2 = f.get(generator);

                                if (generator2 instanceof ChunkProviderOverworld) {
                                    clean(event.world.provider.getDimension(), (ChunkProviderOverworld) generator2);
                                    failed = false;
                                } else {
                                    warn("Found Sponge chunk generator for dim %s, but it does not contain a vanilla overworld provider, but %s", event.world.provider.getDimension(), generator2);
                                }
                            } catch (NoSuchFieldException e) {
                                warn("Could not retrieve ChunkProviderOverworld from Sponge. Could not find field");
                                e.printStackTrace();
                            } catch (IllegalAccessException e) {
                                warn("Could not retrieve ChunkProviderOverworld from Sponge. Could not access field");
                            } catch (ClassNotFoundException e) {
                                warn("Could not find SpongeChunkGeneratorClass");
                            }
                        } else {
                            failed = true;
                            warn("ChunkGenerator is not an instance of ChunkProvider, but %s", generator.getClass());
                        }
                    } else {
                        failed = true;
                        warn("Chunk Provider is not an instance of ChunkProviderServer, but %s", provider.getClass());
                    }
                    if (failed) {
                        disabled.add(event.world.provider.getDimension());

                    }
                }
            }
        }
    }

    private void clean(int dim, ChunkProviderOverworld generator) {
        MapGenStructureData structureData = generator.mineshaftGenerator.structureData;
        NBTTagCompound nbt = structureData.getTagCompound();
        if (nbt.getSize() > 100) {
            FMLLog.info("[MineshaftFixer]Clearing %s structure data entries in dim %s", nbt.getSize(), dim);
            String[] a = nbt.getKeySet().toArray(new String[nbt.getSize()]);
            for (String k : a) {
                nbt.removeTag(k);
            }
            FMLLog.info("[MineshaftFixer]Cleared structure data ");
        }
        Long2ObjectMap<StructureStart> map = generator.mineshaftGenerator.structureMap;
        if (map.size() > 100) {
            map.clear();
            FMLLog.info("[MineshaftFixer]Cleared structure starts");
        }
        structureData.setDirty(true);
    }

    private void warn(String text, Object... data) {
        FMLLog.bigWarning("[MineshaftFixer]" + text, data);
    }
}
