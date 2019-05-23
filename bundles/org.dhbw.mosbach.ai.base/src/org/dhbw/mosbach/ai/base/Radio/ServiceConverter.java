package org.dhbw.mosbach.ai.base.Radio;

import com.google.common.primitives.Bytes;
import com.google.gson.Gson;

import java.nio.ByteBuffer;
import java.util.Arrays;

/*

    Protocol:
      0-8 identify                 |   0-3 int                                4-7 int              7-length  String
      [10,20,30,40,50,60,70,80]    |  <length of length, checksum, Json>    <checksum>  <Json of ServiceInformation>

    identifier : Um den Anfang der sequenz zu identifizieren

    length: länge der gesendeten bytes

    checksum -> hashwert berechnet aus STRING -> <Json of ServiceInformation> "Es ist immernoch eine UDP Verbindung :D"

    <Json> generiertes Json durch Gson von ServiceInformation objekt

 */
public class ServiceConverter {

    public static final byte[] identifier = new byte[]{(byte)10,(byte)20,(byte)30,(byte)40,(byte)50,(byte)60,(byte)70,(byte)80};

    public static ServiceInformation getServiceInformation(byte[] receivedBuffer){

        int index = indexOf(receivedBuffer,identifier);

        byte[] lengthBytes = Arrays.copyOfRange(receivedBuffer, index+0+8,index+8+4);

        int length = ByteBuffer.wrap(lengthBytes).getInt();

        if (length+index+8 > receivedBuffer.length){
            return null;
        }else {
            byte[] protocol = Arrays.copyOfRange(receivedBuffer,index+8,index+length+8);
            return convertByteToServiceInformation(protocol);
        }

    }

    public static byte[] convertServiceInformationToByte(ServiceInformation serviceInformation){

        if (serviceInformation != null) {

            Gson g = new Gson();
            String json = g.toJson(serviceInformation);
            int hash = calclulateHash(json);

            byte[] jsonBytes = json.getBytes();
            byte[] hashBytes = ByteBuffer.allocate(4).putInt(hash).array();

            int length = jsonBytes.length + hashBytes.length + 4;
            byte[] lengthByte = ByteBuffer.allocate(4).putInt(length).array();

            byte[] result = Bytes.concat(identifier,lengthByte, hashBytes, jsonBytes);

            return result;
        }

        return new byte[0];
    }

    public static int indexOf(byte[] outerArray, byte[] smallerArray) {
        for(int i = 0; i < outerArray.length - smallerArray.length+1; ++i) {
            boolean found = true;
            for(int j = 0; j < smallerArray.length; ++j) {
                if (outerArray[i+j] != smallerArray[j]) {
                    found = false;
                    break;
                }
            }
            if (found) return i;
        }
        return -1;
    }

    private static ServiceInformation convertByteToServiceInformation(byte[] receivedData){

        if (receivedData.length > 12) {
            //get Length 0..3
            byte[] lengthBytes = Arrays.copyOfRange(receivedData, 0, 4);

            int length = ByteBuffer.wrap(lengthBytes).getInt();

            if (receivedData.length == length) {


                //Get hash from byte 4-7
                byte[] hashBytes = Arrays.copyOfRange(receivedData, 4, 8);
                int hash = ByteBuffer.wrap(hashBytes).getInt();

                byte[] jsonBytes = Arrays.copyOfRange(receivedData, 8, length);
                String json = new String(jsonBytes);
                System.out.println(json);
                int recHash = calclulateHash(json);
                if (hash == recHash) {
                    Gson g = new Gson();
                    return g.fromJson(json,ServiceInformation.class);
                }
            }

        }

        return null;
    }

    public static int calclulateHash(String jsonString){
        char[] array = jsonString.toCharArray();

        int hash=0;
        for (char c:array) {
            hash+= Math.pow(c,2) % Integer.MAX_VALUE;
        }
        return hash;
    }
}
