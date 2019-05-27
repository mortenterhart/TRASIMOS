package org.dhbw.mosbach.ai.v2.factory;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.dhbw.mosbach.ai.base.Radio.Configuration;
import org.dhbw.mosbach.ai.v2.provider.V2Impl;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

@Component(name = "v2-factory", service = V2Factory.class, immediate = true)
public class V2Factory implements IV2Factory {

    private List<Thread> cars = new ArrayList<>();
    private int id = 0;

    @Activate
    public void activate(ComponentContext context, BundleContext bundleContext, Map<String, ?> properties) {
        System.out.println("V2 Factory booting ...");

        FactoryService.startService();
    }
    
    @Deactivate
    public void deactivate() {
        System.out.println("V2 Factory shutting down ...");
        try {
            for (Thread t : cars) {
                t.join();
            }
        } catch (InterruptedException exc) {
            exc.printStackTrace();
        }
    }
    
    @Override
    public void createV2Cars(int count) {
        if (count <= 0) {
            throw new IllegalArgumentException("Number of V2s to create has to be positive: " + count);
        }
        
        for (int i = id; i < count + id; i++) {
            V2Impl v2 = null;
            try {
                v2 = new V2Impl((long)i,generateRandomLongtitude(),generateRandomLatitude(),generateRandomLongtitude(),generateRandomLatitude(),100);

            System.out.println("Thread with id " + i);

            Thread thread = new Thread(v2);
            cars.add(thread);
            thread.start();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }
        id += count;
    }

    public double generateRandomLatitude(){
        return Math.random()*(Configuration.root11_mapppoint_lat-Configuration.root00_mappoint_lat)+Configuration.root00_mappoint_lat;
    }

    public double generateRandomLongtitude(){
        return Math.random()*(Configuration.root11_mapppoint_lang-Configuration.root00_mappoint_lang)+Configuration.root00_mappoint_lang;
    }
}
