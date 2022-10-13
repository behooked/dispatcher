package com.github.behooked.core;

import java.util.Date;
import java.util.Objects;

import com.github.behooked.api.EventJSON;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;

@Entity
@Table(name = "events")

@NamedQuery(
		name = "com.github.behooked.core.Event.findAll",
		query = "SELECT e FROM Event e")

@NamedQuery(
		name = "com.github.behooked.core.Event.findByName",
		query = "SELECT e FROM Event e WHERE e.name = :eventName")


public class Event {


	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) // generate the primary key value
	private long id;

	@Column(name = "name", nullable = false)
	private String name;

	@Column(name = "timestamp", nullable = false)
	private Date timestamp;

	@Column(name = "data", nullable = false)
	private String data;

	public Event()
	{

	}

	public Event(String name, Date timestamp, String data) {

		this.name = name;
		this.timestamp = timestamp;
		this.data = data;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		}

		if (!(o instanceof Event)) {
			return false;
		}

		final Event event= (Event) o;

		return id == event.id &&
				Objects.equals(name, event.name) &&
				Objects.equals(timestamp, event.timestamp) &&
				Objects.equals(data, event.data);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, name, timestamp, data);
	}

	// convert EventJSON to Event
	public static Event convertToEvent(final EventJSON eventJSON)
	{
		return new Event( eventJSON.getName(),eventJSON.getTimestamp(), eventJSON.getData());
	}
}
