package com.github.behooked.client;

import java.io.IOException;
import java.net.URL;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.testing.junit5.DropwizardClientExtension;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.client.Client;

// wenn nur als test für Notificationresource möglich + dafür auch test Notificationresource von Admin beachten
@ExtendWith(DropwizardExtensionsSupport.class)
public class NotificationSenderTest {

    @Path("/ping")
    public static class PingResource {
        @POST
        public String ping(@HeaderParam("Behooked-Webhook-Secret") final String secret) {
            return "pong";
            
            
        }
    }
    
    private static final DropwizardClientExtension EXT = new DropwizardClientExtension(new PingResource());
    
    @Test
	void testSendNotification() throws IOException {

		final Client client = new JerseyClientBuilder(EXT.getEnvironment()).build("test");
		
		// create parameter
		final  URL url = new URL(EXT.baseUri() + "/ping");
		final  String dummyUrl = url.toString();
		final String secret = "superSecret";
		final String payload = "Test test test.";

		final NotificationSender notificationSender = new NotificationSender(client);
	
		notificationSender.sendNotification(dummyUrl, secret, payload);
    }
}
