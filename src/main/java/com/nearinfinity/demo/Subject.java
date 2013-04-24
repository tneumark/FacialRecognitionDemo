package com.nearinfinity.demo;

import java.util.ArrayList;
import java.util.List;

public class Subject {
	private long id;
	private String name;
	private List<FaceImage> images;
	
	public Subject(long id, String name) {
		this.id = id;
		this.name = name;
		images = new ArrayList<FaceImage>();
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
	
	public List<FaceImage> getFaceImages() {
		return images;
	}
	
	public void addFaceImage(FaceImage faceImage) {
		images.add(faceImage);
	}
}
