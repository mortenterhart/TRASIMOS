package org.dhbw.mosbach.ai.radio.provider;

import org.dhbw.mosbach.ai.base.radio.Configuration;
import org.dhbw.mosbach.ai.base.radio.ServiceInformation;
import org.dhbw.mosbach.ai.radio.api.IRadio;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

import javax.xml.ws.Endpoint;
import java.net.Inet4Address;
import java.util.ArrayList;
import java.util.Map;


@Component(name = "radio", service = IRadio.class)
public class Radio implements Runnable, IRegisterListener, IRadio {

    private BroadcastPublisher radioPublish;
    private Thread radioThread;
    private BroadcastPublisher namePublish;
    private Thread nameThread;
    private BroadcastPublisher webserverPublish;
    private Thread webserverThread;

    private Thread threadRadio;

    private RegisterService registerService;

    private static volatile ArrayList<String> nameServices = new ArrayList<>();
    private static volatile ArrayList<String> webServer = new ArrayList<>();

    @Activate
    public void activate(ComponentContext context, BundleContext bundleContext, Map<String, ?> properties) {
        System.out.println("V2 booting ...");

    }

    @Deactivate
    public void deactivate() {
        System.out.println("V2 shutting down ...");
    }

    public Radio() {

    }

    @Override
    public void run() {

        //Initialize 2 Frequenz
        //      1   -> Radio own ip
        //      2   -> URLs of Nameserver
        //      3   -> URLs of Webserver

        boolean initalized = false;

        while (!initalized) {

            try {


                String url = Configuration.general_https + "0.0.0.0" + Configuration.Radio_Registration_url;
                registerService = new RegisterService();
                registerService.addIRegisterListener(this);
                Object implementor = registerService;
                Endpoint.publish(url, implementor);
                //Create SOAP Webservice for Registration of Service
                //RegisterService.startService(this,Configuration.general_https+"0.0.0.0"+Configuration.Radio_Registration_url);

                initalized = true;

            } catch (Exception exp) {

                System.out.println("Failed to start SOAP register Service " + exp);
            }

        }

        initalized = false;

        while (!initalized) {

            try {

                //Start radioThread and publish local IP
                String localIp = Inet4Address.getLocalHost().getHostAddress();
                ServiceInformation serviceInformation = new ServiceInformation();
                serviceInformation.serviceTyp = Configuration.Radio_ContentType;
                serviceInformation.urls.add(Configuration.general_https + localIp + Configuration.Radio_Registration_url);
                radioPublish = new BroadcastPublisher(Configuration.Radio_multiCastAddress, Configuration.Radio_multiCastPort, serviceInformation, Configuration.Radio_ContentType, Configuration.Radio_Delay_Broadcast);
                radioThread = new Thread(radioPublish);
                radioThread.start();

                initalized = true;

            } catch (Exception exp) {

                System.out.println("Failed to start Nameservice UDP " + exp);
            }

        }

        initalized = false;

        while (!initalized) {

            try {


                ServiceInformation serviceInformationName = new ServiceInformation();
                serviceInformationName.serviceTyp = Configuration.NameService_ContentType;
                //Start publish name Services
                namePublish = new BroadcastPublisher(Configuration.NameService_multiCastAddress, Configuration.NameService_multiCastPort, serviceInformationName, Configuration.NameService_ContentType, Configuration.NameService_Delay_Broadcast);
                nameThread = new Thread(namePublish);
                nameThread.start();

                initalized = true;

            } catch (Exception exp) {

                System.out.println("Failed to start Webserver UDP " + exp);
            }

        }

        initalized = false;

        while (!initalized) {

            try {

                ServiceInformation webInformationName = new ServiceInformation();
                webInformationName.serviceTyp = Configuration.Webserver_ContentType;
                //Start publish name Webserver
                webserverPublish = new BroadcastPublisher(Configuration.Webserver_multiCastAddress, Configuration.Webserver_multiCastPort, webInformationName, Configuration.Webserver_ContentType, Configuration.Webserver_Delay_Broadcast);
                webserverThread = new Thread(webserverPublish);
                webserverThread.start();

                initalized = true;
                System.out.println("__________Started Broadcast Channels succesfull___________");

            } catch (Exception exp) {

                System.out.println("Failed to start Radio " + exp);
            }

        }

        while (true) {

            //do nothing
            try {
                Thread.sleep(0b111110100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    //GetTCP Messages via Oberserver Pattern
    @Override
    public void getNotified(String url, String serviceTyp) {
        switch (serviceTyp) {
            //Adds and sets new url to publisher as Message for UDP Port
            case Configuration.NameService_ContentType:
                nameServices.add(url);
                setNameServiceMessage();
                break;

            case Configuration.Webserver_ContentType:
                webServer.add(url);
                setWebserverMessage();
                break;
        }
    }

    private void setWebserverMessage() {
        ServiceInformation serviceInformation = new ServiceInformation();
        serviceInformation.serviceTyp = Configuration.Webserver_ContentType;
        serviceInformation.urls.addAll(webServer);
        this.webserverPublish.setMessage(serviceInformation);
    }

    private void setNameServiceMessage() {
        ServiceInformation serviceInformation = new ServiceInformation();
        serviceInformation.serviceTyp = Configuration.NameService_ContentType;
        serviceInformation.urls.addAll(nameServices);
        this.namePublish.setMessage(serviceInformation);
    }

    @Override
    public void registerServiceAccess(String url, String serviceTyp) {
        registerService.registerServiceAccess(url, serviceTyp);
    }
}
