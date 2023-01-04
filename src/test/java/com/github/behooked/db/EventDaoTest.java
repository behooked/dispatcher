package com.github.behooked.db;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.github.behooked.core.Event;
import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import io.dropwizard.testing.junit5.DAOTestExtension;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;

@ExtendWith(DropwizardExtensionsSupport.class)
public class EventDaoTest {

	public DAOTestExtension database = DAOTestExtension.newBuilder().addEntityClass(Event.class).build();

	private EventDAO dao;
	private long millis;
	private java.util.Date dummyDate; 

	@BeforeEach
	public void setUp() {

		dao = new EventDAO(database.getSessionFactory());
		millis =86400000;// random date for test purpose 02 January 1970
		dummyDate=new java.util.Date(millis); 

	}

	@Test
	void createEvent(){
		final Event event = database.inTransaction(() -> {
			return dao.create(new Event("eventName",dummyDate,"This is a test."));
		});

		assertEquals("eventName",event.getName());
		assertThat(dao.findById(event.getId())).isEqualTo(Optional.of(event));
	}

	@Test
	void findAll() {
		database.inTransaction(() -> {
			dao.create(new Event("eventA",dummyDate,"This is a test."));
			dao.create(new Event("eventB",dummyDate,"Test test test."));
		});

		final List<Event> list = dao.findAll();
		assertThat(list).extracting("name").containsOnly("eventA", "eventB");

	}
}
