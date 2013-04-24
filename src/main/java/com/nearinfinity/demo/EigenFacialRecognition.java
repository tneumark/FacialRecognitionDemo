
package com.nearinfinity.demo;

import org.openimaj.experiment.dataset.GroupedDataset;
import org.openimaj.experiment.dataset.ListBackedDataset;
import org.openimaj.experiment.dataset.ListDataset;
import org.openimaj.experiment.dataset.MapBackedDataset;
import org.openimaj.feature.DoubleFVComparison;
import org.openimaj.image.FImage;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.MBFImage;
import org.openimaj.image.colour.Transforms;
import org.openimaj.image.processing.face.alignment.FaceAligner;
import org.openimaj.image.processing.face.alignment.RotateScaleAligner;
import org.openimaj.image.processing.face.detection.DetectedFace;
import org.openimaj.image.processing.face.detection.FaceDetector;
import org.openimaj.image.processing.face.detection.keypoints.FKEFaceDetector;
import org.openimaj.image.processing.face.recognition.EigenFaceRecogniser;
import org.openimaj.image.processing.face.recognition.FaceRecognitionEngine;
import org.openimaj.ml.annotation.ScoredAnnotation;
import org.openimaj.util.pair.IndependentPair;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.*;

public class EigenFacialRecognition {
    public static final String IMAGE_SAMPLE_DIR = "./image_samples/";
    //public static final String IMAGE_SAMPLE_DIR = "C:\\Users\\tneumark\\Documents\\apache-tomcat-7.0.39\\webapps\\facialRecognition\\trainingImages";
    public static final Map<String, MBFImage> imageCache = new HashMap<String, MBFImage>();
    private static final int SIZE = 100;

    public static final void main(String[] args) {
        try {
            //Separate the training images from the probe images
            int imageGroupMax = 18;
            float probePercentage = 0.10f;
            Random random = new Random(5000);
            Object[] data = getData(imageGroupMax, probePercentage, random);
            GroupedDataset<String, ListDataset<FImage>, FImage> dataset = (GroupedDataset<String, ListDataset<FImage>, FImage>) data[0];
            List<File> probePaths = (List<File>)data[1];
            int total = dataset.size() + probePaths.size();
            System.out.println("\nTraining Count = " + dataset.size() + "; Probe Count = " + probePaths.size() + "; Probe % = " + probePaths.size() / (float) total* 100 + "\n");

            //Try out different combinations of values to see what works
            //15, 8, 5 with 15 groups and 20% probes
            List<com.nearinfinity.demo.EigenfaceParameterPerformance> performance = new ArrayList<com.nearinfinity.demo.EigenfaceParameterPerformance>();
            for (int numberOfComponents = 15; numberOfComponents <= 15 ; numberOfComponents++) {
                for (float threshold=8f; threshold <= 8f; threshold++) {
                    for (int kNearestNeighbors = 5; kNearestNeighbors <= 5; kNearestNeighbors++) {
                        //Create the face detector and aligner
                        FKEFaceDetector faceDetector = new FKEFaceDetector(SIZE);
                        RotateScaleAligner faceAligner = new RotateScaleAligner();

                        //Create the engine using all the parameters
                        System.out.println("Please wait while I train the engine with numberOfComponents=" + numberOfComponents + ", threshold=" + threshold + ", kNearestNeighbors=" + kNearestNeighbors);
                        FaceRecognitionEngine engine = createAndTrainRecognitionEngine(dataset, faceDetector, faceAligner, numberOfComponents, threshold, kNearestNeighbors);

                        //Try all the probes and keep stats on how well we are matching
                        System.out.println("Now let's validate the accuracy under these parameters\n");
                        int correctMatches = validateAccuracy(probePaths, faceDetector, engine);
                        float percentageCorrect= (float)correctMatches / probePaths.size();

                        //Add this result to the list of results and print it
                        EigenfaceParameterPerformance stat = new EigenfaceParameterPerformance(percentageCorrect,numberOfComponents, threshold, kNearestNeighbors);
                        performance.add(stat);
                        System.out.println("** Result for this combination: " + stat + "\n");

                        //Save the engine
                        engine.save(new File("facialRecognitionEngine"));
                        System.out.println("Saved engine\n");
                    }
                }
            }

            Collections.sort(performance);
            System.out.println("****** Summary of All Combinations From Worst to Best Performance ******");
            for (EigenfaceParameterPerformance stat: performance) {
                System.out.println(stat);
            }



        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static int validateAccuracy(List<File> probePaths, FaceDetector faceDetector, FaceRecognitionEngine engine) throws IOException {
        int correctMatches = 0;
        int incorrectMatches = 0;
        for (File probe: probePaths) {
            MBFImage queryImage = loadImageToCache(probe.getAbsolutePath());
            List<ScoredAnnotation<String>> annotations = computeFindFace(queryImage, engine, faceDetector);
            System.out.println("For probe " + probe.getAbsolutePath() + " annotation is " + annotations);
            if (annotations != null && !annotations.isEmpty()) {
                ScoredAnnotation<String> best = annotations.get(annotations.size() - 1);
                ScoredAnnotation<String> secondBest = null;
                if (annotations.size() >= 2 ) {
                    secondBest = annotations.get(annotations.size() - 2);
                }
                if (probe.getAbsolutePath().contains(best.annotation) || (secondBest != null && probe.getAbsolutePath().contains(secondBest.annotation))) {
                    System.out.println("Correctly matched " + probe.getAbsolutePath() + " to " + best.annotation + ", " + secondBest);
                    correctMatches++;
                }
                else {
                    System.out.println("Incorrectly matched " + probe.getAbsolutePath() + " to " + best.annotation);
                    incorrectMatches++;
                }
            }
            else {
                System.out.println("Did not match anything for " + probe.getAbsolutePath() );
                incorrectMatches++;
            }
            System.out.println();
        }
        return correctMatches;
    }

    private static FaceRecognitionEngine createAndTrainRecognitionEngine(GroupedDataset dataset, FaceDetector faceDetector, FaceAligner faceAligner, int numberOfComponents, float threshold, int kNearestNeighbors) {
        EigenFaceRecogniser<DetectedFace, String> recogniser = EigenFaceRecogniser.create(numberOfComponents, faceAligner, kNearestNeighbors, DoubleFVComparison.EUCLIDEAN,  threshold);
        FaceRecognitionEngine engine = FaceRecognitionEngine.create(faceDetector,recogniser);
        engine.train(dataset);
        return engine;
    }

    private static List<ScoredAnnotation<String>> computeFindFace(MBFImage queryImage, FaceRecognitionEngine engine, FaceDetector<? extends DetectedFace, FImage> faceDetector) throws IOException {
        FImage convertedQueryImage = Transforms.calculateIntensity(queryImage);
        List<? extends DetectedFace> detectedFaces = faceDetector.detectFaces(convertedQueryImage);
        if (detectedFaces != null && !detectedFaces.isEmpty())      {
            DetectedFace detectedFace = detectedFaces.get(0);

            List<IndependentPair<FImage, List<ScoredAnnotation<String>>>>  results = engine.recognise(convertedQueryImage);
            List<ScoredAnnotation<String>> scoredAnnotations = results.get(0).secondObject();
            Collections.sort(scoredAnnotations);
            return scoredAnnotations;
        }
        return null;
    }

    private static Object[] getData(int imageGroupMax, float probePercentage, Random random) throws IOException {
        //This object holds both pieces of data.  The first element holds training data, second element holds probes
        Object[] data = new Object[2];

        //Create the objects to hold the training data and the probe data
        MapBackedDataset<String, ListDataset<FImage>, FImage> groupedDataset = new MapBackedDataset<String,ListDataset<FImage>, FImage>();
        List<File> probeFiles = new ArrayList<File>();

        //Create the formatter to handle the padding of the directory names
        NumberFormat formatter = NumberFormat.getNumberInstance();
        formatter.setMinimumIntegerDigits(2);

        for (int index=1; index<=imageGroupMax; index++) {
            File file = new File(IMAGE_SAMPLE_DIR + "/s" + formatter.format(index));
            if (file.exists()) {
                System.out.println("\nLooking in directory: " + file.getAbsolutePath());
                ListBackedDataset<FImage> list = new ListBackedDataset();
                FilenameFilter filter = new FilenameFilter() {
                    public boolean accept(File directory, String fileName) {
                        return fileName.endsWith(".jpg");
                    }
                };
                File[] files = file.listFiles(filter);
                for (File fileInDirectory: files) {
                    if (selectedForTraining(probePercentage, random)) {
                        System.out.println("ADDED TRAINING:" + fileInDirectory.getAbsolutePath());
                        MBFImage loadedImage = loadImage(fileInDirectory.getAbsolutePath());
                        FImage trainingImage = Transforms.calculateIntensity(loadedImage);
                        list.add(trainingImage);
                    }
                    else {
                        System.out.println("ADDED PROBE: " + fileInDirectory.getAbsolutePath());
                        probeFiles.add(fileInDirectory);
                    }
                }
                groupedDataset.getMap().put(file.getName(), list);
            }
        }
        data[0] = groupedDataset;
        data[1] = probeFiles;
        return data;
    }

    private static boolean selectedForTraining(float probePercentage, Random random) {
        if ((float)random.nextInt(100) / 100 >= probePercentage) {
            return true;
        }
        return false;
    }


    private static MBFImage loadImageToCache(String filename) throws IOException {
        MBFImage image = null;
        if (!imageCache.containsKey(filename)) {
            image = ImageUtilities.readMBF(new File(filename));
            imageCache.put(filename, image);
            System.out.println("Added to CACHE: " + filename);
        }
        else {
            System.out.println("Got from CACHE: " + filename);
        }
        return imageCache.get(filename);
    }

    private static MBFImage loadImage(String filename) throws IOException {
        MBFImage image = ImageUtilities.readMBF(new File(filename));
        return image;
    }


}