package com.github.behooked.api;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.github.behooked.core.Event;

public class EventJSON {

	private long id;

	private String name;

	private Date timestamp;

	private String data;

	@JsonCreator
	public EventJSON(@JsonProperty("id") long id, @JsonProperty("name")String name, @JsonProperty("timestamp")Date timestamp, @JsonProperty("data") String data) {  
		this.id = id;
		this.name = name;
		this.timestamp = timestamp;
		this.data = data;

	}

	@JsonProperty
	public long getId() {
		return id;
	}

	@JsonProperty
	public void setId(long id) {
		this.id = id;
	}

	@JsonProperty
	public String getName() {
		return name;
	}
	@JsonProperty
	public void setName(String eventName) {
		this.name = eventName;
	}

	@JsonProperty
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	public Date getTimestamp() {
		return timestamp;
	}

	@JsonProperty
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	@JsonProperty
	public String getData() {
		return data;
	}

	@JsonProperty
	public void setData(String data) {
		this.data = data;
	}

	// convert Event to EventJSON

	public static EventJSON from(final Event event)
	{
		return new EventJSON(event.getId(), event.getName(),event.getTimestamp(), event.getData());
	}

}
