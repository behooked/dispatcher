package com.github.behooked.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.node.ArrayNode;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

public class AdministrationInformant {

	private final Client client;
	private static final Logger LOGGER = LoggerFactory.getLogger(AdministrationInformant.class);


	public AdministrationInformant(final Client client)
	{
		this.client = client;
	}

	public ArrayNode getClientData(final String url, final String eventName)
	{

		Response response= client.target(url).request(MediaType.APPLICATION_JSON)
		.post(Entity.json(eventName));
		
		ArrayNode clientData= response.readEntity(ArrayNode.class);
		LOGGER.info("------------Request send to Administration to receive respective client-data. event-name = {} ----------------", eventName);
		
		return clientData;

	}  
}
