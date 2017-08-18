package io.github.skyshayde;

import com.google.gson.Gson;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.FMLInjectionData;
import net.minecraftforge.fml.relauncher.Side;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@Mod(modid = ModSync.MODID, version = ModSync.VERSION)
public class ModSync {
    public static final String MODID = "modsync";
    public static final String VERSION = "0.1";
    public static String serverUrl = "localhost:3030";
    public static Configuration config;

    String mcVer = (String) FMLInjectionData.data()[4];
    File mcDir = (File) FMLInjectionData.data()[6];
    private File modsDir = new File(mcDir, "mods");
    private File v_modsDir = new File(mcDir, "mods/" + mcVer);

    public static void syncConfig() { // Gets called from preInit
        try {
            // Load config
            config.load();

            // Read props from config
            Property serverUrlProp = config.get(Configuration.CATEGORY_GENERAL, // What category will it be saved to, can be any string
                    "serverURL", // Property name
                    "localhost:3030", // Default value
                    "What server should mods sync from"); // Comment

            serverUrl = serverUrlProp.getString(); // Get the boolean value, also set the property value to boolean
        } catch (Exception e) {
            // Failed reading/writing, just continue
        } finally {
            // Save props to config IF config changed
            if (config.hasChanged()) config.save();
        }
    }



    @EventHandler
    public void init(FMLInitializationEvent event) {
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
//        DepLoader.Dependency dep = new DepLoader.Dependency(new File("test"),"repo","false",new DepLoader.VersionedFile("test",Pattern.compile("*"))),"test",false);
        config = new Configuration(event.getSuggestedConfigurationFile());
        syncConfig();
        DepLoader.load();
        if (event.getSide() == Side.CLIENT) {
            doServer();
            serverThread sThread = new serverThread();
            sThread.start();
        }
    }

    public void doServer() {
        System.out.println(buildJson());
    }

    public String buildJson() {
        List<File> list = new LinkedList<File>();
        List<String> finalList = new LinkedList<String>();
        list.addAll(Arrays.asList(modsDir.listFiles()));
        list.addAll(Arrays.asList(v_modsDir.listFiles()));
        for (File file : list) {
            if (!file.getName().endsWith(".jar") && !file.getName().endsWith(".zip"))
                continue;
            finalList.add(file.getName());

        }
        Gson gson = new Gson();
        return gson.toJson(finalList);
    }

}
