package com.github.behooked.client;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

public class AdministrationInformant {

	private final Client client;
	//private final String ADMINISTRATION_URL = "http://localhost:8081/api/notifications";


	public AdministrationInformant(final Client client)
	{
		this.client = client;
	}

	public Response sendNotification(final String url, final String eventName, final Long eventId)
	{

		Response response= client.target(url).request(MediaType.APPLICATION_JSON).header("Behooked-Dispatcher-Notification-EventId", eventId)
		.post(Entity.json(eventName));
		
		return response;

	}     

}
