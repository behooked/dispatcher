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

		Response response= client.target(url).request(MediaType.APPLICATION_JSON).header("Behooked-Webhook-Secret", secret)
		.post(Entity.json(payload));
		// secret is send via Header to check authentification for resource update
		return response;
	}
}
