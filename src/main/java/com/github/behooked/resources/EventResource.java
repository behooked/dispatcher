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
import jakarta.ws.rs.core.MediaType;

import java.util.List;
import java.util.stream.Collectors;

import com.github.behooked.api.EventJSON;
import com.github.behooked.core.Event;


@Path("events")
@Produces(MediaType.APPLICATION_JSON)
public class EventResource {
	
	private final EventDAO eventDAO;

	
	public EventResource(final EventDAO eventDAO) {
		this.eventDAO = eventDAO;
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
	@UnitOfWork public EventJSON getEventById(@PathParam("id") final long id) {
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

	        final Event event = Event.convertToEvent(eventJson);
	        final Event createdEvent = eventDAO.create(event);
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
