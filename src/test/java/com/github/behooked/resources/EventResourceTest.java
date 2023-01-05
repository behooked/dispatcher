package com.github.behooked.resources;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

import com.github.behooked.core.Event;
import com.github.behooked.api.EventJSON;
import com.github.behooked.client.AdministrationInformant;
import com.github.behooked.db.EventDAO;

import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import io.dropwizard.testing.junit5.ResourceExtension;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@ExtendWith(DropwizardExtensionsSupport.class)
public class EventResourceTest {

	private static final EventDAO EVENT_DAO = mock(EventDAO.class);
	private static final AdministrationInformant ADMIN_INFORMANT = mock(AdministrationInformant.class);

	private static final ResourceExtension EXT = ResourceExtension.builder() //
			.addResource(new EventResource(EVENT_DAO, ADMIN_INFORMANT)) //
			.build();
	
	
	private Event event;
	private long millis;   // random date for test purpose 02 January 1970
	private java.util.Date dummyDate; 
	private 

	@BeforeEach
	void setUp()
	{
		millis =86400000;
		dummyDate= new java.util.Date(millis);
		event = new Event("eventName",dummyDate,"This is a test.");
		event.setId(1L);
	}

	@AfterEach
	void tearDown()
	{
		reset(EVENT_DAO);
	}

	
	@Test
	void listEvents() {

		List<Event> list = new ArrayList<Event>(); 
		list.add(event); 

		when(EVENT_DAO.findAll()).thenReturn(list);
		final Response response = EXT.target("/events").request() .get();

		verify(EVENT_DAO,Mockito.times(1)).findAll(); 
		assertThat(response.getStatusInfo(),is(Response.Status.OK));		

	}

	@Test
	void getEventByIdSuccess() {
		when(EVENT_DAO.findById(1L)).thenReturn(Optional.of(event));

		EventJSON found = EXT.target("/events/1").request().get(EventJSON.class);
		assertThat(found.getId(), is(event.getId()));
		verify(EVENT_DAO).findById(1L);  
	}

	@Test 
	void getByIdReturnsOK() {
		when(EVENT_DAO.findById(1L)).thenReturn(Optional.of(event));
		final Response response = EXT.target("/events/1").request().get();
		assertThat(response.getStatusInfo(), is(Response.Status.OK));
	}

	@Test
	void getEventByIdNotFound() {
		when(EVENT_DAO.findById(2L)).thenReturn(Optional.empty()); // returns an empty Optional instance

		final Response response = EXT.target("/events/2").request().get();
		assertThat(response.getStatus(), is(Response.Status.NOT_FOUND.getStatusCode())); 
		verify(EVENT_DAO).findById(2L); 
	}


	@Test
	void createEvent() {

		when(EVENT_DAO.create(any(Event.class))).thenReturn(event);
		
		final Response response = EXT.target("/events")  
				.request(MediaType.APPLICATION_JSON_TYPE)
				.post(Entity.json(new EventJSON(1l,"eventName",dummyDate,"This is a test.")));

		EventJSON newEvent = response.readEntity(EventJSON.class);

		verify(ADMIN_INFORMANT).sendNotification(anyString(), anyLong());

		assertThat(response.getStatusInfo(), is(Response.Status.OK)); 
		assertThat(newEvent.getName(), is("eventName")); 
		assertThat(newEvent.getId(), is(1L)); 
		assertThat(newEvent.getTimestamp()).isEqualTo(dummyDate);
		assertThat(newEvent.getData()).isEqualTo("This is a test.");
	}
}
