package com.github.behooked.resources;

import com.github.behooked.db.EventDAO;

import io.dropwizard.hibernate.UnitOfWork;
import jakarta.validation.Valid;
import jakarta.ws.rs.ClientErrorException;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.core.MediaType;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.behooked.api.EventJSON;
import com.github.behooked.client.AdministrationInformant;
import com.github.behooked.core.Event;


@Path("events")
@Produces(MediaType.APPLICATION_JSON)
public class EventResource {

	private final EventDAO eventDAO;
	private final AdministrationInformant administrationInformant;

	private static final Logger LOGGER = LoggerFactory.getLogger(EventResource.class);

	public EventResource(final EventDAO eventDAO, final Client client) {
		this.eventDAO = eventDAO;
		this.administrationInformant = new AdministrationInformant (client);
	}

	@GET
	@UnitOfWork
	public List<EventJSON> listEvents(){

		return eventDAO.findAll().stream()
				.map(e -> EventJSON.from(e))
				.collect(Collectors.toList());
	}

	/*
	@GET
	@Path("{name}")
	@UnitOfWork
	public EventJSON getEventByName(@PathParam("name") final String name) { 

		final Event event = eventDAO.findByName(name); 

		return EventJSON.from(event);  } 

	 */


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
	public EventJSON createEvent(@Valid final EventJSON eventJson) 
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


		final Event event = Event.convertToEvent(eventJson);
		final Event createdEvent = eventDAO.create(event);

		EventJSON eventJSON =  EventJSON.from(createdEvent);

		LOGGER.info("------------Received a notification from Kafka-Connector:  event-name = {} event-id = {} ----------------", eventJSON.getName(), event.getId());
		//	LOGGER.info(String.format("----------- EventId was: %s ----------------", event.getId()));

	    // send eventId and eventName to administration
		administrationInformant.sendNotification(eventJSON.getName(), createdEvent.getId());


		LOGGER.info("------------Request send to Administration to receive respective client-data.------------- ");

		return EventJSON.from(createdEvent);


	}



	@DELETE
	@Path("{id}")
	@UnitOfWork
	public void deleteEventByID(@PathParam("id") final long id){
		final Event event= findSafely(id);
		eventDAO.delete(event);
	}



}
