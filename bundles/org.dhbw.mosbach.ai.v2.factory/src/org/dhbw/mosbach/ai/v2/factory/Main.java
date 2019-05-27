package org.dhbw.mosbach.ai.v2.factory;

public class Main {

    public static void main(String args[]) {
        V2Factory v2Factory = new V2Factory();
        v2Factory.activate(null, null, null);
        v2Factory.createV2Cars(5);
        while (true) {
            ;
        }
    }
}
