package org.dhbw.mosbach.ai.radio.provider;

import org.dhbw.mosbach.ai.radio.api.IRadio;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.xml.ws.Endpoint;
import java.util.ArrayList;

@WebService(endpointInterface = "org.dhbw.mosbach.ai.radio.api.IRadio")
public class RegisterService implements IRadio, IRegisterProvider {

    private ArrayList<IRegisterListener> registerListeners = new ArrayList<>();

    @WebMethod
    @Override
    public void registerServiceAccess(String serviceTyp, String url) {
        System.out.println("Service registered" + serviceTyp + ":" + url);
        notifyListener(serviceTyp, url);

    }

    @Override
    public void notifyListener(String serviceTyp, String url) {
        for (IRegisterListener reg : registerListeners) {
            reg.getNotified(serviceTyp, url);
        }
    }

    @Override
    public void addIRegisterListener(IRegisterListener iRegisterListener) {
        registerListeners.add(iRegisterListener);
    }

    //Add Listener and publish to URL
    public static void startService(IRegisterListener registerListener, String url) {
        RegisterService registerService = new RegisterService();
        registerService.addIRegisterListener(registerListener);
        Endpoint.publish(url, registerService);
    }
}
