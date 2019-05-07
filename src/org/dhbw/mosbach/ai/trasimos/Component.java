package org.dhbw.mosbach.ai.trasimos;

import org.osgi.service.http.HttpService;

public class Component {
    private HttpService service;
    
    public void setHttp(HttpService service) {
        this.service = service;
    }
    
    public void shutdown() {
        System.out.println("Shutdown component");
    }
    
    public void startup() {
        System.out.println("Startup component");
        try {
            service.registerServlet("/ExampleServlet", new ExampleServlet(), null, null);
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }
}
