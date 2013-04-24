package com.nearinfinity.demo;

public class FileExtensionTest {
	public static void main(String[] args) {
		String fileName = "c:/p/ttttt/ttt.jpg";
		String extension = fileName.substring(fileName.lastIndexOf("."));
		System.out.println(extension);		
	}
}
