
package com.nearinfinity.demo;

import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.*;

import org.openimaj.experiment.dataset.*;
import org.openimaj.feature.DoubleFVComparison;
import org.openimaj.image.DisplayUtilities;
import org.openimaj.image.FImage;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.MBFImage;
import org.openimaj.image.colour.Transforms;
import org.openimaj.image.processing.edges.CannyEdgeDetector;
import org.openimaj.image.processing.face.alignment.ScalingAligner;
import org.openimaj.image.processing.face.detection.DetectedFace;
import org.openimaj.image.processing.face.detection.HaarCascadeDetector;
import org.openimaj.image.processing.face.recognition.EigenFaceRecogniser;
import org.openimaj.image.processing.face.recognition.FaceRecognitionEngine;
//import org.openimaj.ml.clustering.kmeans.fast.FastFloatKMeans;
import org.openimaj.ml.annotation.ScoredAnnotation;
import org.openimaj.util.pair.IndependentPair;
import org.openimaj.video.Video;
import org.openimaj.video.VideoDisplay;
import org.openimaj.video.capture.VideoCapture;
import org.openimaj.video.capture.VideoCaptureException;


public class FacialRecognition {
    public static final String IMAGE_SAMPLE_DIR = "./image_samples/";
    public static final int IMAGE_GROUP_MAX = 50;
    public static final String SAMPLE_IMAGE = IMAGE_SAMPLE_DIR + "s01/01.jpg";
    public static final String QUERY_IMAGE = IMAGE_SAMPLE_DIR + "s14/01.jpg";
    private static final int HEIGHT = 100;
    private static final int WIDTH = 100;

    public static final void main(String[] args) {
        try {
            //MBFImage image = loadImage(SAMPLE_IMAGE);
            //computeImageClusters(image);
            //findKeyFeatures(image);
            computeVideo();
            //computeHistogram(image);

            //MBFImage queryImage = loadImage(QUERY_IMAGE);
            //computeSIFT(image, queryImage);

            //computeFindFace(image, queryImage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void computeFindFace(MBFImage image, MBFImage queryImage) throws IOException {
        HaarCascadeDetector faceDetector = new HaarCascadeDetector(WIDTH);
        FImage convertedImage = Transforms.calculateIntensity(image);
        FImage convertedQueryImage = Transforms.calculateIntensity(queryImage);

        GroupedDataset dataset = getGroupedFaces();
        System.out.println("There are " + dataset.size() + " faces in the training set");
        ScalingAligner<DetectedFace> aligner = new ScalingAligner<DetectedFace>(WIDTH, HEIGHT);

        int numberOfComponents=12;
        float threshold=5f;
        int kNearestNeighbors= (int)(.20 * dataset.size());

        EigenFaceRecogniser<DetectedFace, String> recogniser = EigenFaceRecogniser.create(numberOfComponents, aligner, kNearestNeighbors, DoubleFVComparison.EUCLIDEAN,  threshold);
        FaceRecognitionEngine engine = FaceRecognitionEngine.create(faceDetector,recogniser);
        engine.train(dataset);

        DetectedFace detectedFace = faceDetector.detectFaces(convertedQueryImage).get(0);

        List<IndependentPair<FImage, List<ScoredAnnotation<String>>>>  results = engine.recognise(convertedQueryImage);
        for (IndependentPair<FImage, List<ScoredAnnotation<String>>> pair: results) {
            List<ScoredAnnotation<String>> scoredAnnotations = pair.secondObject();
            for (ScoredAnnotation<String> annotation: scoredAnnotations) {
                System.out.println(annotation.annotation + " has confidence of " + annotation.confidence);
            }
        }


    }
//
//    private static FImage getFaceFromImage(FImage image) {
//        ResizeProcessor resize = new ResizeProcessor(WIDTH,HEIGHT);
//        HaarCascadeDetector haarCascadeDetector = new HaarCascadeDetector(WIDTH);
//        List<DetectedFace> haarDetectedFaces = haarCascadeDetector.detectFaces(image);
//        DetectedFace detectedFace = haarDetectedFaces.get(0);
//        System.out.println("Original face size is " + detectedFace.getBounds());
//
//        FImage imageToResize = detectedFace.getFacePatch();
//        System.out.println("Face patch size is " + imageToResize.getBounds());
//        resize.processImage(imageToResize);
//        System.out.println("Resized face patch size is " + imageToResize.getBounds());
//
//        if (imageToResize.getBounds().getWidth() == WIDTH && imageToResize.getBounds().getHeight() == HEIGHT) {
//            return imageToResize;
//        }
//        else {
//            System.out.println("Image did not resize to the correct size");
//            return null;
//        }
//    }

//
//    private static List<? extends DetectedFace> getFaces() throws IOException {
//        List<DetectedFace> list = new ArrayList<DetectedFace>();
//        ResizeProcessor resize = new ResizeProcessor(WIDTH,HEIGHT);
//        NumberFormat formatter = NumberFormat.getNumberInstance();
//        formatter.setMinimumIntegerDigits(2);
//        for (int index=1; index<=1; index++) {
//            File file = new File("image_samples/s" + formatter.format(index));
//            System.out.println("Looking in directory: " + file.getAbsolutePath());
//            File[] files = file.listFiles();
//            for (File fileInDirectory: files) {
//                System.out.println("\n\nProcessing file: " + fileInDirectory.getAbsolutePath());
//                HaarCascadeDetector haarCascadeDetector = new HaarCascadeDetector(WIDTH);
//                MBFImage loadedImage = loadImage(fileInDirectory.getAbsolutePath());
//                FImage trainingImage = Transforms.calculateIntensity(loadedImage);
//
//                List<DetectedFace> haarDetectedFaces = haarCascadeDetector.detectFaces(trainingImage);
//                DetectedFace detectedFace = haarDetectedFaces.get(0);
//                System.out.println("Original face size is " + detectedFace.getBounds());
//
//                FImage imageToResize = detectedFace.getFacePatch();
//                System.out.println("Face patch size is " + imageToResize.getBounds());
//                resize.processImage(imageToResize);
//                System.out.println("Resized face patch size is " + imageToResize.getBounds());
//
//                DetectedFace resizedDetectedFace = new DetectedFace(imageToResize.getBounds(), imageToResize, 1);
//                System.out.println("Resized detected face size is " + resizedDetectedFace.getBounds());
//                if (imageToResize.getBounds().getWidth() == WIDTH && imageToResize.getBounds().getHeight() == HEIGHT) {
//                    list.add(resizedDetectedFace);
//                }
//                else {
//                    System.out.println("Image did not resize to the correct size");
//                }
//            }
//        }
//        return list;
//    }

    private static GroupedDataset<String, ListDataset<FImage>, FImage> getGroupedFaces() throws IOException {
        MapBackedDataset<String, ListDataset<FImage>, FImage> groupedDataset = new MapBackedDataset<String,ListDataset<FImage>, FImage>();
        NumberFormat formatter = NumberFormat.getNumberInstance();
        formatter.setMinimumIntegerDigits(2);

        for (int index=1; index<=IMAGE_GROUP_MAX; index++) {
            File file = new File("image_samples/s" + formatter.format(index));
            System.out.println("Looking in directory: " + file.getAbsolutePath());
            ListBackedDataset<FImage> list = new ListBackedDataset();
            File[] files = file.listFiles();
            for (File fileInDirectory: files) {
                if (!fileInDirectory.getAbsolutePath().contains("01.jpg")) {
                    System.out.println("\n\nProcessing file: " + fileInDirectory.getAbsolutePath());
                    MBFImage loadedImage = loadImage(fileInDirectory.getAbsolutePath());
                    FImage trainingImage = Transforms.calculateIntensity(loadedImage);
                    list.add(trainingImage);
                }
            }
            groupedDataset.getMap().put(file.getName(), list);
        }
        return groupedDataset;
    }
//
//    private static void computeSIFT(MBFImage image, MBFImage queryImage) {
//        DoGSIFTEngine engine = new DoGSIFTEngine();
//        LocalFeatureList<Keypoint> queryKeypoints = engine.findFeatures(queryImage.flatten());
//        LocalFeatureList<Keypoint> targetKeypoints = engine.findFeatures(image.flatten());
//        LocalFeatureMatcher<Keypoint> matcher = new BasicMatcher<Keypoint>(80);
//        matcher.setModelFeatures(queryKeypoints);
//        matcher.findMatches(targetKeypoints);
//        //MBFImage basicMatches = MatchingUtilities.drawMatches(queryImage , image , matcher . getMatches () , RGBColour.RED ) ;
//        //DisplayUtilities.display ( basicMatches ) ;
//
//        AffineTransformModel fittingModel = new AffineTransformModel(5);
//        RANSAC<Point2d, Point2d> ransac = new RANSAC<Point2d, Point2d>(fittingModel, 1500, new RANSAC.PercentageInliersStoppingCondition(0.5), true);
//        matcher = new ConsistentLocalFeatureMatcher2d<Keypoint>(new FastBasicKeypointMatcher<Keypoint>(8), ransac);
//        matcher.setModelFeatures(queryKeypoints);
//        matcher.findMatches(targetKeypoints);
//        MBFImage consistentMatches = MatchingUtilities.drawMatches(queryImage, image, matcher.getMatches(), RGBColour.RED);
//        DisplayUtilities.display(consistentMatches);
//    }
//
    private static void computeVideo() throws VideoCaptureException {
        Video<MBFImage> video = new VideoCapture(500, 500);
        VideoDisplay<MBFImage> display = VideoDisplay.createVideoDisplay(video);
        for (MBFImage image : video) {
            DisplayUtilities.displayName(image.process(new CannyEdgeDetector(1)), "videoFrames");
        }
    }
//
//    private static void findKeyFeatures(MBFImage image) {
//        //Define different colors to distinguish the each detector's results
//        Float[] haarColor = new Float[]{100f, 100f, 255f};
//        Float[] fkeColor = new Float[]{50f, 40f, 15f};
//        Float[] sandeepColor = new Float[]{250f, 140f, 150f};
//
//        //The size of each keypoint
//        int keypointSize = 5;
//
//        //Convert the color image to greyscale
//        FImage convertedImage = Transforms.calculateIntensity(image);
//
//        //Find using Haar cascade
//        HaarCascadeDetector haarCascadeDetector = new HaarCascadeDetector(100);
//        List<DetectedFace> haarDetectedFaces = haarCascadeDetector.detectFaces(convertedImage);
//        for (DetectedFace face : haarDetectedFaces) {
//            //image.drawShape(new Rectangle(face.getBounds()), haarColor);
//        }
//
//        FKEFaceDetector fkeFaceDetector = new FKEFaceDetector();
//        List<KEDetectedFace> fkeDetectedFaces = fkeFaceDetector.detectFaces(convertedImage);
//        for (KEDetectedFace face : fkeDetectedFaces) {
//            Rectangle faceBounds = new Rectangle(face.getBounds());
//            image.drawShape(faceBounds, fkeColor);
//            for (FacialKeypoint keypoint : face.getKeypoints()) {
//                keypoint.position.translate((float) faceBounds.minX(), (float) faceBounds.minY());
//                image.drawPoint(keypoint.position, fkeColor, keypointSize);
//            }
//        }
//
//        SandeepFaceDetector sandeepFaceDetector = new SandeepFaceDetector();
//        List<CCDetectedFace> sandeepDetectedFaces = sandeepFaceDetector.detectFaces(image);
//        for (CCDetectedFace face : sandeepDetectedFaces) {
//            image.drawShape(face.getBounds(), sandeepColor);
//        }
//
//        //Show the results
//        DisplayUtilities.display(image);
//
//    }

    private static MBFImage loadImage(String filename) throws IOException {
        MBFImage image = ImageUtilities.readMBF(new File(filename));
        System.out.println("Color Space: " + image.colourSpace);
        return image;
    }
//
//    private static void computeHistogram(MBFImage image) {
//        HistogramModel model = new HistogramModel(4, 4, 4);
//        model.estimateModel(image);
//        MultidimensionalHistogram histogram = model.histogram;
//        System.out.println(histogram);
//    }
//
//    private static void computeImageClusters(MBFImage imageInput) {
//        int dimensionality = 3;
//        int k = 8;
//
//        MBFImage image = ColourSpace.convert(imageInput, ColourSpace.CIE_Lab);
//        System.out.println("Color Space: " + image.colourSpace);

//        final FastFloatKMeansCluster kmeans = new FastFloatKMeansCluster(dimensionality, k, true);
//
//        // Generate some random data to cluster
//        final float[][] data = image.getPixelVectorNative(new float[image.getWidth() * image.getHeight()][3]);
//
//        // Perform the clustering
//        final FloatCentroidsResult result = kmeans.cluster(data);
//        float[][] centroids = result.getCentroids();
//        for (float[] centroid : centroids) {
//            System.out.println(Arrays.toString(centroid));
//        }
//
//        for (int y = 0; y < image.getHeight(); y++) {
//            for (int x = 0; x < image.getWidth(); x++) {
//                float[] pixel = image.getPixelNative(x, y);
//                int centroid = result.defaultHardAssigner().assign(pixel);
//                image.setPixelNative(x, y, centroids[centroid]);
//            }
//        }
//
//        //image = ColourSpace.convert(image, ColourSpace.RGB);
//
//        GreyscaleConnectedComponentLabeler labeler = new GreyscaleConnectedComponentLabeler();
//        List<ConnectedComponent> components = labeler.findComponents(image.flatten());
//        int i = 1;
//        Float[] color = new Float[]{255f, 0f, 0f};
//        for (ConnectedComponent component : components) {
//            if (component.calculateArea() < 50)
//                continue;
//            //image.drawText("G" + i, component.calculateCentroidPixel(), HersheyFont.TIMES_MEDIUM, 20, color);
//            //DisplayUtilities.display(component);
//            i++;
//        }
//
//        image = ColourSpace.convert(image, ColourSpace.RGB);
//        DisplayUtilities.display(image);
//
//    }
}


//        EigenFaceFeature.Extractor<DetectedFace> extractor = new EigenFaceFeature.Extractor(15,aligner);
//        extractor.train(faces);
//
//        FaceFVComparator<EigenFaceFeature,DoubleFV> comparator = new FaceFVComparator<EigenFaceFeature, DoubleFV>(DoubleFVComparison.EUCLIDEAN);
//        FaceSimilarityEngine<DetectedFace,EigenFaceFeature,FImage> engine =  FaceSimilarityEngine.create(faceDetector, extractor,comparator);
//        engine.setQuery(extractedQueryImage, "TheQuery");
//        engine.setTest(extractedFaceImage, "TheImage");
//        engine.performTest();
//
//        SimilarityMatrix matrix = engine.getSimilarityMatrix(true);
//        Map similarityDictionary = engine.getSimilarityDictionary();