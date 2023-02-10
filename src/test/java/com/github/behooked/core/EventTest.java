package com.github.behooked.core;


import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.assertj.core.api.Assertions.assertThat;
import com.github.behooked.api.EventJSON;

public class EventTest {

	private static long MILLIS =86400000;// random date for test purpose 02 January 1970
	private java.util.Date dummyDate=new java.util.Date(MILLIS); 

	@Test
	public void testEvent(){

		final Event event = new Event("eventName",dummyDate,"This is a test.");

		assertThat(event.getName()).isEqualTo("eventName");
		assertThat(event.getTimestamp()).isEqualTo(dummyDate);
		assertThat(event.getData()).isEqualTo("This is a test.");
	}


	@Test
	public void convertSuccessfully() {

		EventJSON eventJson = new EventJSON(1l,"eventName",dummyDate,"This is a test.");

		assertTrue(Event.convertToEvent(eventJson) instanceof Event);
	}
}
