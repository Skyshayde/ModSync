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
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Mod(modid = ModSync.MODID, version = ModSync.VERSION, acceptableRemoteVersions = "*")
public class ModSync {
    public static final String MODID = "modsync";
    public static final String VERSION = "0.1";
    public static String serverUrl = "http://168.235.103.57:8080";
    public static String serverPort = "8080";
    public static Configuration config;
    public List<Map> modslist = new ArrayList<Map>();
    public List<Map> cfgList = new ArrayList<Map>();
    String mcVer = (String) FMLInjectionData.data()[4];
    File mcDir = (File) FMLInjectionData.data()[6];
    private File modsDir = new File(mcDir, "mods");
    private File v_modsDir = new File(mcDir, "mods/" + mcVer);

    public static void syncClientConfig() {
        try {
            // Load config
            config.load();

            // Read props from config
            Property serverUrlProp = config.get(Configuration.CATEGORY_GENERAL, // What category will it be saved to, can be any string
                    "serverURL", // Property name
                    "http://localhost:8080", // Default value
                    "What server should mods sync from"); // Comment

            serverUrl = serverUrlProp.getString(); // Get the boolean value, also set the property value to boolean
        } catch (Exception e) {
            // Failed reading/writing, just continue
        } finally {
            // Save props to config IF config changed
            if (config.hasChanged()) config.save();
        }
    }

    public static void syncServerConfig() {
        try {
            // Load config
            config.load();

            // Read props from config
            Property portProp = config.get(Configuration.CATEGORY_GENERAL, // What category will it be saved to, can be any string
                    "port", // Property name
                    "8080", // Default value
                    "What port should the server be run on"); // Comment

            serverPort = portProp.getString(); // Get the boolean value, also set the property value to boolean
            Property serverUrlProp = config.get(Configuration.CATEGORY_GENERAL, // What category will it be saved to, can be any string
                    "serverURL", // Property name
                    "http://168.235.103.57:8080", // Default value
                    "What server should mods sync from"); // Comment

            serverUrl = serverUrlProp.getString(); // Get the boolean value, also set the property value to boolean
        } catch (Exception e) {
            // Failed reading/writing, just continue
        } finally {
            // Save props to config IF config changed
            if (config.hasChanged()) config.save();
        }
    }

    public static URLConnection connectServer(String url) {
        try {
            URL urlVar = new URL(url);
            return urlVar.openConnection();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        if (event.getSide() == Side.CLIENT) {
            System.out.println("ModSync started on Client side");
            config = new Configuration(event.getSuggestedConfigurationFile());
            syncServerConfig();
            URLConnection serverConnection = connectServer(serverUrl);
            System.out.println("Connecting to " + serverUrl);
            String json = "";
            try {
                json = IOUtils.toString(serverConnection.getInputStream());
                Gson gson = new Gson();
                Map result = gson.fromJson(json, Map.class);
                modslist = (List<Map>) result.get("mods");
                cfgList = (List<Map>) result.get("config");
                ModDownloader.downloadList(serverUrl, modslist, cfgList);
            } catch (IOException e) {
                e.printStackTrace();
            }

//            DepLoader.load();
        }
        // Switch to side.server in production
        if (event.getSide() == Side.SERVER) {
            System.out.println("ModSync started on Server side");
            config = new Configuration(event.getSuggestedConfigurationFile());
            syncServerConfig();
            serverThread sThread = new serverThread();
            sThread.start();
        }
    }

}
