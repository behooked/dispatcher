package com.github.behooked.client;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MediaType;

public class AdministrationInformant {

	private final Client client;
	private final String ADMINISTRATION_URL = "http://localhost:8081/api/notifications";


	public AdministrationInformant(final Client client)
	{
		this.client = client;
	}

	public void sendNotification(final String eventName, final Long eventId)
	{

		client.target(ADMINISTRATION_URL).request(MediaType.APPLICATION_JSON).header("Behooked-Dispatcher-Notification-EventId", eventId)
		.post(Entity.json(eventName));

	}     

}
