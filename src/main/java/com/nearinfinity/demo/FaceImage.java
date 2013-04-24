package com.nearinfinity.demo;

import java.sql.Timestamp;

public class FaceImage {
	private long id;
	private long trainingSubjectId;
	private Timestamp timeCollected;
	private double latitude;
	private double longitude;

	public FaceImage(long id, long trainingSubjectId, Timestamp timeCollected, double latitude, double longitude) {
		this.id = id;
		this.trainingSubjectId = trainingSubjectId;
		this.timeCollected = timeCollected;
		this.latitude = latitude;
		this.longitude = longitude;		
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getTrainingSubjectId() {
		return trainingSubjectId;
	}

	public void setTrainingSubjectId(long trainingSubjectId) {
		this.trainingSubjectId = trainingSubjectId;
	}

	public Timestamp getTimeCollected() {
		return timeCollected;
	}

	public void setTimeCollected(Timestamp timeCollected) {
		this.timeCollected = timeCollected;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
}
