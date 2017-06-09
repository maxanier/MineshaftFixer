package de.maxanier.mineshaftfixer;


import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

@Mod(
        modid = MineshaftFixer.MOD_ID,
        name = MineshaftFixer.MOD_NAME,
        version = MineshaftFixer.VERSION,
        serverSideOnly = true,
        acceptableRemoteVersions = "*"
)
public class MineshaftFixer {

    public static final String MOD_ID = "mineshaftfixer";
    public static final String MOD_NAME = "MineshaftFixer";
    public static final String VERSION = "0.1";


    @Mod.Instance(MOD_ID)
    public static MineshaftFixer INSTANCE;

    /**
     * This is the second initialization event. Register custom recipes
     */
    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new Listener());
    }
}
