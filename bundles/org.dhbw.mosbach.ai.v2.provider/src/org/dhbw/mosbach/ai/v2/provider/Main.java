package org.dhbw.mosbach.ai.v2.provider;

import java.net.UnknownHostException;

//TEEeEEEeestttttt
public class Main {

    public static void main(String args[]){
        try {
            V2Impl v2 = new V2Impl((long)1,0,0,0,0);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        while (true);
    }
}
