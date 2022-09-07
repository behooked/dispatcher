package com.github.behooked.resources;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import com.github.behooked.api.EventJSON;
import com.github.behooked.client.NotificationSender;
import com.github.behooked.core.Event;
import com.github.behooked.db.EventDAO;

import io.dropwizard.hibernate.UnitOfWork;

import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.client.Client;

@Path("dispatcher")
public class NotificationResource {

	private EventDAO eventDao;
	private NotificationSender notificationSender;
	private String payload;

	private static final Logger LOGGER = LoggerFactory.getLogger(NotificationResource.class);

	public NotificationResource(EventDAO eventDao, Client client){

		this.eventDao = eventDao;
		this.notificationSender = new NotificationSender(client);

	}


	private Event findSafely(final long eventId) {
		return eventDao.findById(eventId).orElseThrow(() -> new NotFoundException("No such event")); }

	@POST
	@UnitOfWork
	public void receiveNotification (final ArrayNode arrayClientData, @HeaderParam("Behooked-Administration-EventId")final Long eventId) {

		
		String test = arrayClientData.get(0).get("url").toString();

		LOGGER.info("------------Received a Notification from Administration-Service.---------------- ");
		LOGGER.info(String.format("EventId was: %s", eventId));
		LOGGER.info(String.format("Test if data was received sucessfuly. First url of client-data was: %s", test));

	}
}
