package com.github.behooked.resources;

import com.github.behooked.db.EventDAO;

import io.dropwizard.hibernate.UnitOfWork;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.ClientErrorException;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.github.behooked.api.EventJSON;
import com.github.behooked.client.AdministrationInformant;
import com.github.behooked.client.NotificationSender;
import com.github.behooked.core.Event;


@Path("events")
@Produces(MediaType.APPLICATION_JSON)
public class EventResource {

	private final EventDAO eventDAO;
	private final AdministrationInformant administrationInformant;
	private final String adminUrl;
	private final NotificationSender notificationSender;

	private static EventJSON RECEIVED_EVENT;
	private static Long EVENT_ID;

	private static final Logger LOGGER = LoggerFactory.getLogger(EventResource.class);

	public EventResource(final EventDAO eventDAO, final AdministrationInformant adminInformant, final String adminUrl, final NotificationSender notificationSender) {
		this.eventDAO = eventDAO;
		this.administrationInformant = adminInformant;
		this.adminUrl= adminUrl;
		this.notificationSender= notificationSender;

	}

	// necessary for testing
	public static void setEVENT_ID(Long event_Id) {
		EVENT_ID = event_Id;
	}

	@GET
	@UnitOfWork
	public List<EventJSON> listEvents(){

		return eventDAO.findAll().stream()
				.map(e -> EventJSON.from(e))
				.collect(Collectors.toList());
	}

	@GET
	@Path("{id}")
	@UnitOfWork 
	public EventJSON getEventById(@PathParam("id") final long id) {

		final Event event = findSafely(id);

		return EventJSON.from(event);  }


	private Event findSafely(final long eventId) {
		return eventDAO.findById(eventId).orElseThrow(() -> new NotFoundException("No such event")); }






	@POST
	@UnitOfWork
	public EventJSON createEvent(@NotNull @Valid final EventJSON eventJson) 
	{
		if ((eventJson.getName() == null))
		{
			throw new ClientErrorException("Bad Request. The field 'name' must not be null", 400);
		}

		if ((eventJson.getTimestamp() == null))
		{
			throw new ClientErrorException("Bad Request. The field 'timestamp' must not be null", 400);
		}

		if ((eventJson.getData() == null))
		{
			throw new ClientErrorException("Bad Request. The field 'data' must not be null", 400);
		}
		RECEIVED_EVENT= eventJson;

		LOGGER.info("------------Received a notification. event-name = {} ---------------", RECEIVED_EVENT.getName());

		// create event
		final Event createdEvent = eventDAO.create(Event.convertToEvent(eventJson));

		EVENT_ID = createdEvent.getId();
		LOGGER.info("------------New event created in database. event-name = {} event-id = {} ------------", createdEvent.getName(), createdEvent.getId());
		LOGGER.info("------------Request send to Administration to receive respective client-data. event-name = {} event-id = {} ----------------", createdEvent.getName(), createdEvent.getId());

		// send eventId and eventName to administration service to get respective client-data
		administrationInformant.sendNotification(adminUrl, createdEvent.getName(), createdEvent.getId());

		return EventJSON.from(createdEvent);


	}

	@POST
	@Path("{dispatch}")
	@UnitOfWork
	public void receiveNotification (final ArrayNode arrayClientData, @HeaderParam("Behooked-Administration-EventId")final Long eventId) {

		LOGGER.info("------------Received a Notification with Client-Data from Administration-Service. event-id was: {} Number of registered Clients that need to be informed: {}---------------- " ,eventId, arrayClientData.size());

		if (eventId == EVENT_ID)
		{
			for (int i = 0; i < arrayClientData.size(); ++i) {

				String url = arrayClientData.get(i).get("url").toString();
				String secret = arrayClientData.get(i).get("secret").toString(); 

				// remove quotes
				url =url.substring(1, url.length() - 1);
				secret =secret.substring(1, secret.length() - 1);

				//send data to registered clients
				notificationSender.sendNotification(url, secret, RECEIVED_EVENT.getData());
			}  

			LOGGER.info("Registered clients have been informed about event. event-name: {}---------------- " , RECEIVED_EVENT.getName()); 
		}
		else
		{
			LOGGER.info("Event-Id was: {} But expected event-id = {} Received client-data does not belong to this event. Data has not been send.---------------- " , eventId,EVENT_ID); 
		}  
	}

	@DELETE
	@Path("{id}")
	@UnitOfWork
	public void deleteEventByID(@PathParam("id") final long id){
		final Event event= findSafely(id);
		eventDAO.delete(event);
	}



}
