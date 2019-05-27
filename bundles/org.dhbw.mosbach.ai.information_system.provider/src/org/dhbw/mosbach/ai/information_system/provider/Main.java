package org.dhbw.mosbach.ai.information_system.provider;

public class Main {
    public static void main(String args[]){
        InformationSystemImpl informationSystem = new InformationSystemImpl();
        informationSystem.activate(null,null,null);

        InformationSystemImpl informationSystem2 = new InformationSystemImpl();
        informationSystem2.activate(null,null,null);

        while (true);
    }
}
