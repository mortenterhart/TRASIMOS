package org.dhbw.mosbach.ai.radio.provider;

public interface IRegisterProvider {
    public void notifyListener(String serviceTyp, String url);
    public void addIRegisterListener(IRegisterListener iRegisterListener);
}
