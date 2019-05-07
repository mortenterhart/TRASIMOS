package org.dhbw.mosbach.ai.trasimos;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * @author 6694964
 */

public class Activator implements BundleActivator {

    private static BundleContext context;

    private static BundleContext getContext() {
        return context;
    }

    public void start(BundleContext bundleContext) throws Exception {
        Activator.context = bundleContext;
        System.out.println("Hello World!");
    }

    public void stop(BundleContext bundleContext) throws Exception {
        System.out.println("Good Bye World!");
    }
}
