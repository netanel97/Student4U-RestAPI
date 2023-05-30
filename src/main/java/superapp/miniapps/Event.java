package superapp.miniapps;

import java.util.Arrays;
import java.util.List;

public class Event {

	private List<String> participants;
	private String subject;
	private String content;
	private String startTime;
	private String endTime;
	private String date;
	private EventType type;
	private String objectid;

	public Event() {
		super();
	}

	public Event(List<String> participants, String subject, String content, String startTime, String endTime,
			String date, EventType type, String objectid) {
		super();
		this.participants = participants;
		this.subject = subject;
		this.content = content;
		this.startTime = startTime;
		this.endTime = endTime;
		this.date = date;
		this.type = type;
		this.objectid = objectid;
	}

	public List<String> getParticipants() {
		return participants;
	}

	public void setParticipants(List<String> participants) {
		this.participants = participants;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public EventType getType() {
		return type;
	}

	public void setType(EventType type) {
		this.type = type;
	}

	public String getObjectid() {
		return objectid;
	}

	public void setObjectid(String objectid) {
		this.objectid = objectid;
	}

	@Override
	public String toString() {
		return "Event [participants=" + participants + ", subject=" + subject + ", content=" + content + ", startTime="
				+ startTime + ", endTime=" + endTime + ", date=" + date + ", type=" + type + ", objectid=" + objectid
				+ "]";
	}

}
