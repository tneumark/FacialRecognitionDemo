
package com.nearinfinity.demo;

import org.openimaj.feature.FeatureExtractor;
import org.openimaj.image.DisplayUtilities;
import org.openimaj.image.FImage;
import org.openimaj.image.MBFImage;
import org.openimaj.image.colour.Transforms;
import org.openimaj.image.processing.face.detection.DetectedFace;
import org.openimaj.image.processing.face.detection.FaceDetector;
import org.openimaj.image.processing.face.detection.HaarCascadeDetector;
import org.openimaj.image.processing.face.feature.EigenFaceFeature;
import org.openimaj.image.processing.face.recognition.EigenFaceRecogniser;
import org.openimaj.image.processing.face.recognition.FaceRecognitionEngine;
import org.openimaj.image.processor.ImageProcessor;
import org.openimaj.image.typography.general.GeneralFont;
import org.openimaj.math.geometry.shape.Rectangle;
import org.openimaj.ml.annotation.ScoredAnnotation;
import org.openimaj.util.pair.IndependentPair;
import org.openimaj.video.Video;
import org.openimaj.video.VideoDisplay;
import org.openimaj.video.capture.VideoCapture;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.text.AttributedString;
import java.util.Iterator;
import java.util.List;

public class VideoHaarCascade {

    public static final void main(String[] args)  {
        try {
            final Video<MBFImage> video = new VideoCapture(500, 500);
            final GeneralFont font = new GeneralFont("Times New Roman", 36);
            final Float[] color = new Float[] {100f, 255f, 50f};
            final FaceRecognitionEngine<DetectedFace, EigenFaceFeature.Extractor<DetectedFace>, String> engine = FaceRecognitionEngine.load(new File("facialRecognitionEngine"));
            final FaceDetector<DetectedFace, FImage> fd = new HaarCascadeDetector(100);
            final String[] lastMatchName = new String[1];
            final Long[] lastMatchTime = new Long[1];
            lastMatchName[0] = "Unknown";
            lastMatchTime[0] = System.currentTimeMillis();
            final ImageProcessor<MBFImage> imageProcessor = new ImageProcessor<MBFImage>() {
                @Override
                public void processImage(MBFImage image) {
                    try {
                       List<DetectedFace> faces = fd.detectFaces(Transforms.calculateIntensity(image));

                        Iterator<DetectedFace> iterator = faces.iterator();
                        if (iterator.hasNext() ) {
                            DetectedFace face = iterator.next();
                            if (System.currentTimeMillis() - lastMatchTime[0] > 750) {
                                lastMatchTime[0] = System.currentTimeMillis();
                                List<IndependentPair<DetectedFace, List<ScoredAnnotation<String>>>> matches = engine.recognise(face.getFacePatch());
                                lastMatchName[0] = "Unknown";
                                if (matches != null && matches.size() >= 1) {
                                   List<ScoredAnnotation<String>> score = matches.get(0).getSecondObject();
                                   if (score != null && score.size() >= 1) {
                                       lastMatchName[0] = score.get(0).annotation + " (confidence=" + score.get(0).confidence*100+ "%)";
                                   }
                                }
                            }
                            Rectangle bounds = face.getBounds();
                            for (int count=1; count<=7; count++ ) {
                                image.drawShape(bounds, color);
                                bounds.setBounds((float)bounds.getTopLeft().getX()+ count*.2f, (float)bounds.getTopLeft().getY()+count*.2f, (float)bounds.getWidth() - .4f*count, (float)bounds.getHeight()-.4f*count);
                            }
                            image.drawText(lastMatchName[0], 20, 20, font, 20, color);
                        }
                    }
                    catch (Exception e) {
                            e.printStackTrace();
                    }
                }
            };
            for (MBFImage image : video) {
                JFrame videoFrames = DisplayUtilities.displayName(image.process(imageProcessor), "videoFrames");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

