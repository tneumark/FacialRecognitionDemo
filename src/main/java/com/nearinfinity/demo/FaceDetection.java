package com.nearinfinity.demo;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;

import org.openimaj.image.FImage;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.MBFImage;
import org.openimaj.image.colour.Transforms;
import org.openimaj.image.processing.face.detection.DetectedFace;
import org.openimaj.image.processing.face.detection.FaceDetector;
import org.openimaj.image.processing.face.detection.HaarCascadeDetector;
import org.openimaj.image.processing.face.detection.keypoints.FKEFaceDetector;
import org.openimaj.image.processing.face.detection.keypoints.FacialKeypoint;
import org.openimaj.image.processing.face.detection.keypoints.KEDetectedFace;
import org.openimaj.math.geometry.shape.Rectangle;


public class FaceDetection {
    public static void main(String[] args) throws IOException {
    	NumberFormat formatter = NumberFormat.getIntegerInstance();
    	formatter.setMinimumIntegerDigits(2);
        for (int subjectId=1; subjectId <= 50; subjectId++) {
        	for (int imageId=1; imageId <= 15; imageId++) {
        		String fileName = "images\\gt_db\\s" + formatter.format(subjectId) + "\\" + formatter.format(imageId) + ".jpg"; 
        		System.out.println("Extracting " + fileName);
        		extractImage("./webroot/", "./scaled/", subjectId, imageId, fileName, 80,200);
        	}
        }
        
    }
    
    public static void extractImage(String rootDir, String scaledDir, long subjectId, long imageId, String fileName, int haarMinSize, int scaledSize) throws IOException {
    	FileInputStream input = new FileInputStream(fileName);
        MBFImage image = ImageUtilities.readMBF(input);
        BufferedImage detectedFacesImage = ImageIO.read(new File(fileName));
        //ImageUtils.displayImage(detectedFacesImage);
        FaceDetector<DetectedFace, FImage> fd = new HaarCascadeDetector(haarMinSize);
        List<DetectedFace> faces = fd.detectFaces(Transforms.calculateIntensity(image));
        System.out.println("# Found faces, one per line.");
        System.out.println("# <x>, <y>, <width>, <height>");
        Iterator<DetectedFace> iterator = faces.iterator(); 
        BufferedImage extractFaceImage = null;
        if (iterator.hasNext() ) {
            DetectedFace face = iterator.next();
            Rectangle bounds = face.getBounds();
//            extractFaceImage = detectedFacesImage.getSubimage((int)bounds.x, (int)bounds.y, (int)bounds.width, (int)bounds.height);    
//            writeImage(extractFaceImage, rootDir, "extracted", subjectId, imageId);
            
            
//            BufferedImage scaledBufferedImage = scale(extractFaceImage, scaledSize);
//            String scaledFileName = scaledDir + subjectId + "-" + imageId + ".jpg";
//            System.out.println("scaledFileName = " + scaledFileName);
//            ImageIO.write(scaledBufferedImage, "jpg", new File(scaledFileName)); 
            Graphics g = detectedFacesImage.createGraphics();
            g.setColor(Color.GREEN);
            g.drawRect((int)bounds.x, (int)bounds.y, (int)bounds.width, (int)bounds.height);
            System.out.println(bounds.x + ";" + bounds.y + ";" + bounds.width + ";" + bounds.height);
        }
        else {
        	Graphics g = detectedFacesImage.createGraphics();
            g.setColor(Color.GREEN);
            g.drawString("No Image Detected", 20, 20);      	
        } 
        writeImage(detectedFacesImage, rootDir, "detected", subjectId, imageId); 
//        
//        FaceDetector < KEDetectedFace , FImage > fdK = new FKEFaceDetector () ;
//        List < KEDetectedFace > facesK = fdK.detectFaces ( Transforms.calculateIntensity ( image ) ) ;
//        if (!facesK.isEmpty()) {
//        	System.out.println("Found face");
//        	KEDetectedFace detectedKeyFace = facesK.get(0);
//        	FacialKeypoint[] keypoints = detectedKeyFace.getKeypoints();
//        	for (FacialKeypoint keypoint: keypoints) {
//        		System.out.println("keypoint:" + keypoint);
//        	}
//        }
 
    }

	private static BufferedImage scale(BufferedImage extractFaceImage, int size) {
		Image image = extractFaceImage.getScaledInstance(size, size, java.awt.Image.SCALE_SMOOTH);
		BufferedImage bufferedImage = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
	    Graphics g = bufferedImage.createGraphics();
        g.drawImage(image, 0, 0, new Color(0,0,0), null);
        g.dispose();
		return bufferedImage;
	}

	private static void writeImage(BufferedImage imageToWrite, String rootDir, String subDir, long subjectId, long imageId) throws IOException {
        String directoryName = rootDir + "/s" + subjectId + "/" + subDir + "/";
        File directorys = new File(directoryName);
        System.out.println("Dir is " + directoryName);
        if (!directorys.exists()) {
        	System.out.println("Creating dir " + directoryName);
        	directorys.mkdirs();
        }
        System.out.println("Writing image of size (" + imageToWrite.getWidth() + "," + imageToWrite.getHeight()+ ")");
        File file = new File(directoryName + imageId + ".jpg");
        ImageIO.write(imageToWrite, "jpg", file);
        System.out.println("Done writing image to "  + file.getAbsolutePath());
	}
}
