package org.dhbw.mosbach.ai.base;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

    @Override
    public void start(BundleContext arg0) throws Exception {
        System.out.println("Base bundle starting");
    }

    @Override
    public void stop(BundleContext arg0) throws Exception {
        System.out.println("Base bundle stopping");
    }
}
