package io.github.skyshayde;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraftforge.fml.relauncher.FMLInjectionData;
import org.apache.commons.codec.digest.DigestUtils;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created by skysh on 8/17/2017.
 */
public class mcServer extends AbstractHandler {
    String mcVer = (String) FMLInjectionData.data()[4];
    File mcDir = (File) FMLInjectionData.data()[6];
    private File modsDir = new File(mcDir, "mods");
    private File v_modsDir = new File(mcDir, "mods/" + mcVer);
    private File cfgDir = new File(mcDir, "config");

    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        // Gives list of mods if url has no path
        if (request.getRequestURL().toString().split("/").length == 3) {
            response.setContentType("text/html; charset=utf-8");

            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().println(buildJson());

            baseRequest.setHandled(true);
        }

    }

    public String buildJson() {
        Map<String, List> json = new HashMap<String, List>();

        List<Map> modsList = new LinkedList<Map>();
        try {
            Files.walk(Paths.get(modsDir.getPath())).filter(Files::isRegularFile).forEach(File -> {
                String path = modsDir.toPath().relativize(File).toString();
                String hash = "";
                try {
                    hash = DigestUtils.sha1Hex(new FileInputStream(File.toFile()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Map<String, String> map = new HashMap<String, String>();
                map.put("path", path);
                map.put("hash", hash);
                modsList.add(map);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        json.put("mods", modsList);

        List<Map> cfgList = new LinkedList<Map>();
        try {
            Files.walk(Paths.get(cfgDir.getPath())).filter(Files::isRegularFile).forEach(File -> {
                String path = cfgDir.toPath().relativize(File).toString();
                String hash = "";
                try {
                    hash = DigestUtils.sha1Hex(new FileInputStream(File.toFile()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Map<String, String> map = new HashMap<String, String>();
                map.put("path", path);
                map.put("hash", hash);
                cfgList.add(map);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        json.put("config", cfgList);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(json);
    }
}