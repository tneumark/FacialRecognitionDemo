
package com.nearinfinity.demo;

import org.openimaj.image.DisplayUtilities;
import org.openimaj.image.FImage;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.MBFImage;
import org.openimaj.image.colour.Transforms;
import org.openimaj.image.processing.face.detection.CCDetectedFace;
import org.openimaj.image.processing.face.detection.DetectedFace;
import org.openimaj.image.processing.face.detection.HaarCascadeDetector;
import org.openimaj.image.processing.face.detection.SandeepFaceDetector;
import org.openimaj.image.processing.face.detection.keypoints.FKEFaceDetector;
import org.openimaj.image.processing.face.detection.keypoints.FacialKeypoint;
import org.openimaj.image.processing.face.detection.keypoints.KEDetectedFace;
import org.openimaj.math.geometry.shape.Rectangle;

import java.io.File;
import java.io.IOException;
import java.util.List;


public class FindKeyFacialPoints {
    public static final String IMAGE_SAMPLE_DIR = "./image_samples/";
    public static final String SAMPLE_IMAGE = IMAGE_SAMPLE_DIR + "s03/02.jpg";


    public static final void main(String[] args) {
        try {
            MBFImage image = loadImage(SAMPLE_IMAGE);
            findKeyFeatures(image);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void findKeyFeatures(MBFImage image) {
        //Define different colors to distinguish the each detector's results
        Float[] fkeColor = new Float[] {1f, .99f, .99f};

        //The size of each keypoint
        int keypointSize = 5;

        //Convert the color image to greyscale
        FImage convertedImage = Transforms.calculateIntensity(image);

        FKEFaceDetector fkeFaceDetector = new FKEFaceDetector();
        List<KEDetectedFace> fkeDetectedFaces = fkeFaceDetector.detectFaces(convertedImage);
        for (KEDetectedFace face : fkeDetectedFaces) {
            Rectangle faceBounds = new Rectangle(face.getBounds());
            image.drawShape(faceBounds, fkeColor);
            for (FacialKeypoint keypoint : face.getKeypoints()) {
                keypoint.position.translate((float) faceBounds.minX(), (float) faceBounds.minY());
                image.drawPoint(keypoint.position, fkeColor, keypointSize);
            }
        }

        //Show the results
        DisplayUtilities.display(image);

    }

    private static MBFImage loadImage(String filename) throws IOException {
        MBFImage image = ImageUtilities.readMBF(new File(filename));
        return image;
    }
}
