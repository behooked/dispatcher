package com.github.behooked.db;

import java.util.List;
import java.util.Optional;

import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import com.github.behooked.core.Event;

import io.dropwizard.hibernate.AbstractDAO;


public class EventDAO extends AbstractDAO<Event> {

	public EventDAO(SessionFactory sessionFactory) {
		super(sessionFactory);

	}

	public Event create(final Event event) {
		return persist(event);
	}

	@SuppressWarnings("unchecked")
	public List<Event> findAll()
	{
		final Query<Event> namedQuery = (Query<Event>) namedQuery("com.github.behooked.core.Event.findAll");

		return list(namedQuery);  
	}


	public Optional<Event> findById(Long id) {
		return Optional.ofNullable(get(id));
	}


		public void delete(Event event){
			currentSession().delete(event);
		}	
	}
