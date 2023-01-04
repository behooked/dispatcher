package com.github.behooked.api;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.behooked.core.Event;

import io.dropwizard.jackson.Jackson;

public class EventJsonTest {


	private static final ObjectMapper MAPPER = Jackson.newObjectMapper();

	private Event event;;

	private static long MILLIS=86400000;// random date for test purpose 02 January 1970
	private java.util.Date dummyDate=new java.util.Date(MILLIS); 


	@Test
	public void testEventJSON(){

		final EventJSON eventJSON = new EventJSON(1l,"eventName",dummyDate,"This is a test.");

		assertThat(eventJSON.getName()).isEqualTo("eventName");
		assertThat(eventJSON.getId()).isEqualTo(1);
		assertThat(eventJSON.getTimestamp()).isEqualTo(dummyDate);
		assertThat(eventJSON.getData()).isEqualTo("This is a test.");
	}

	@Test
	public void convertSuccessfully() {
		event = new Event("eventName",dummyDate,"This is a test.");
		assertTrue(EventJSON.from(event) instanceof EventJSON);

	}

	@Test
	void serializesToJSON() throws Exception {
		final EventJSON eventJSON = new EventJSON(1l,"eventName",dummyDate,"This is a test.");

		final String expected = MAPPER.writeValueAsString(
				MAPPER.readValue(getClass().getResource("/event.json"), EventJSON.class));

		assertThat(MAPPER.writeValueAsString(eventJSON)).isEqualTo(expected);
	}


	@Test
	public void deserializesFromJSON() throws Exception {
		final EventJSON event = new EventJSON(1l,"eventName",dummyDate,"This is a test.");

		EventJSON deserialized = MAPPER.readValue(getClass().getResource("/event.json"), EventJSON.class);
		assertThat(deserialized.getName()).isEqualTo(event.getName());
		assertThat(deserialized.getId()).isEqualTo(event.getId());
		assertThat(deserialized.getTimestamp()).isEqualTo(event.getTimestamp());
		assertThat(deserialized.getData()).isEqualTo(event.getData());

	}
}
