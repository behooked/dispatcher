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
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.core.MediaType;


@Path("dispatcher")
@Produces(MediaType.APPLICATION_JSON) 
public class NotificationResource {

	private static final Logger LOGGER = LoggerFactory.getLogger(NotificationResource.class);
	
	private EventDAO eventDao;
	private NotificationSender notificationSender;
	private String payload;



	public NotificationResource(EventDAO eventDao, Client client){

		this.eventDao = eventDao;
		this.notificationSender = new NotificationSender(client);
		
	}



	@POST
	@UnitOfWork
	public void receiveNotification (final ArrayNode arrayClientData, @HeaderParam("Behooked-Administration-EventId")final Long eventId) {
    
		Long receivedEventId = eventId;
		

		String test = arrayClientData.get(0).get("url").toString();

		LOGGER.info("------------Received a Notification with Client-Data from Administration-Service. Event-Id was: {}---------------- " , receivedEventId);
		
		
		// TEST
		LOGGER.info(String.format("Test Zugriff auf ArrayNode. Von Administration gesendete Url ist: %s", test));
		
		
		
		/* TEST mit name und create Event
		 * 
		 * 
		Event event = eventDao.findByName(receivedName);
		Date date = new Date(2323223232L);
		Event testEvent = new Event("test-event", date, "My message.");
		Event createdTestEvent = eventDao.create(testEvent);
		LOGGER.info(String.format("Test Zugriff auf DB in NotificationResource. Event-Name ist: %s", createdTestEvent.getName()));
	
		*/
			
		
		// Access EventDao to get respective event + convert event to JSON - required to get payload for respective event from database
		
		Event event = findSafely(receivedEventId);
		LOGGER.info(String.format("EventDao accessed. To check successful acces list event-name: %s", event.getName()));
		
		
		final EventJSON eventJson= EventJSON.from(event);

		LOGGER.info(String.format("Test: Event converted to json: %s", eventJson.getName()));
		
		
	}
		
		// get payload from database and inform registered Clients
	
		/*
		if(event !=null)	
			
		{

		
			payload = event.getData();

			for (int i = 0; i < arrayClientData.size(); ++i) {
				JsonNode clientData = arrayClientData.get(i);
				String url = clientData.get("url").toString();
				String secret = clientData.get("secret").toString(); // besser JsonNode

				// send payload to registered client including secret for authentification for resource update

				notificationSender.sendNotification(url, secret, payload);
				
				LOGGER.info("Registered clients have been informed about event."); 

			}
		}

		else
		{
			// Logging information

			LOGGER.info("Event with that EventId does not exist."); 
			LOGGER.info(String.format("EventId was: %s", eventId));
		}

*/
		private Event findSafely(final long eventId) {
			return eventDao.findById(eventId).orElseThrow(() -> new NotFoundException("No such Event"));
		
	}
}
