package io.github.skyshayde;


import com.google.gson.Gson;
import net.minecraftforge.fml.relauncher.FMLInjectionData;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by skysh on 8/17/2017.
 */
public class mcServer extends AbstractHandler {
    String mcVer = (String) FMLInjectionData.data()[4];
    File mcDir = (File) FMLInjectionData.data()[6];
    private File modsDir = new File(mcDir, "mods");
    private File v_modsDir = new File(mcDir, "mods/" + mcVer);

    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        System.out.println(new Gson().toJson(request.getRequestURL().toString().split("/")));
        if (request.getRequestURL().toString().split("/").length == 3) {
            response.setContentType("text/html; charset=utf-8");
            System.out.println(request.getRequestURL());

            // Declare response status code
            response.setStatus(HttpServletResponse.SC_OK);

            // Write back response
            response.getWriter().println(buildJson());

            // Inform jetty that this request has now been handled
            baseRequest.setHandled(true);
        } else {
            if (request.getRequestURL().toString().split("/").length > 3) {
                String fileName = request.getRequestURL().toString().split("/")[3];
                response.setContentType("application/java-archive\n");
                // Declare response status code
                response.setStatus(HttpServletResponse.SC_OK);
                FileInputStream in = new FileInputStream(modsDir+"/"+fileName);

                response.getOutputStream().write(in.read());

                // Inform jetty that this request has now been handled
                baseRequest.setHandled(true);
            }
        }
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