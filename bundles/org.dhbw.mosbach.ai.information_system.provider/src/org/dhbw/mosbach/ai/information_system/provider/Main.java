package org.dhbw.mosbach.ai.information_system.provider;

public class Main {
    public static void main(String args[]){
        InformationSystemImpl informationSystem = new InformationSystemImpl();
        informationSystem.activate(null,null,null);
        informationSystem.postConstruct();
    }
}
