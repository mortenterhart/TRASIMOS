package org.dhbw.mosbach.ai.webserver.provider;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class MapServlet extends HttpServlet {

    private static final long serialVersionUID = -1294221965043437009L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        /*InputStream fileStream = new FileInputStream("");
        byte[] content = fileStream.readAllBytes();
        fileStream.close();
        
        response.getWriter().write(new String(content));*/
        
        InputStream mapFile = request.getServletContext().getResourceAsStream("/WEB-INF/index.html");
        System.out.println(mapFile);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}
