package com.github.behooked;

import io.dropwizard.core.Application;
import io.dropwizard.core.setup.Bootstrap;
import io.dropwizard.core.setup.Environment;

public class DispatcherApplication extends Application<DispatcherConfiguration> {

    public static void main(final String[] args) throws Exception {
        new DispatcherApplication().run(args);
    }

    @Override
    public String getName() {
        return "Dispatcher";
    }

    @Override
    public void initialize(final Bootstrap<DispatcherConfiguration> bootstrap) {
        // TODO: application initialization
    }

    @Override
    public void run(final DispatcherConfiguration configuration,
                    final Environment environment) {
        // TODO: implement application
    }

}
