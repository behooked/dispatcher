package com.github.behooked.resources;



import io.dropwizard.hibernate.UnitOfWork;

import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

// This resource is used for testing purpose only. ExternalResourceTestClient functions as client when data is send to the administration service. 

@Produces(MediaType.APPLICATION_JSON)
@Path("/testClient")
public class ExternalResourceTestClient {

		private Long eventId;
		
		@POST
		@Path("{dummyAdministrationService}")
		@UnitOfWork
		public void testReceiveNotificationFromAdminInformant(  final String eventName, @HeaderParam("Behooked-Dispatcher-Notification-EventId") final Long eventId) 
		{

				this.eventId= eventId;
		}
}
