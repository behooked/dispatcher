package com.github.behooked.client;


import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

public class NotificationSender {


	private final Client client;


	public NotificationSender(final Client client)
	{
		this.client = client;
	}


	public Response sendNotification(final String url, final String secret, final String payload)
	{
		// secret is send via Header so that the client can check authentication of the sender
		return client.target(url).request(MediaType.APPLICATION_JSON).header("X-Behooked-Webhook-Secret", secret)
				.post(Entity.json(payload));
	}
}
