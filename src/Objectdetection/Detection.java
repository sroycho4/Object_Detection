package Objectdetection;

import org.opencv.dnn.Dnn;
import org.opencv.dnn.Net;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfRect2d;
import org.opencv.core.Point;
import org.opencv.core.Rect2d;
import org.opencv.core.Scalar;
import org.opencv.core.Size;

public class Detection {

    public static String model_weights;
    public static String model_config;
    public static String current_dir;
    public static String class_file_name_dir;
    public static String output_path;
    public static List<String> classes;
    public static List<String> output_layers;
    public static String input_path;
    public static List<String> layer_names;
    public static Net network;
    public static Size size;
    public static Integer height;
    public static Integer width;
    public static Integer channels;
    public static Scalar mean;
    public static Mat image;
    public static Mat blob;
    public static List<Mat> outputs;
    public static List<Rect2d> boxes;
    public static List<Float> confidences;
    public static List<Integer> class_ids;
    public static String outputFileName;
    public static boolean save;
    public static boolean errors;
    public static long t0;
    public static long t1;
    public static int n = 0;

    public Detection(String inputPath, String outputPath, Integer image_size, String outputFileName) {
        t0 = System.currentTimeMillis();
        Detection.input_path = inputPath;
        Detection.output_path = outputPath;
        Detection.outputFileName = outputFileName;
        boxes = new ArrayList<>();
        classes = new ArrayList<>();
        class_ids = new ArrayList<>();
        layer_names = new ArrayList<>();
        confidences = new ArrayList<>();
        double[] means = {0.0, 0.0, 0.0};
        mean = new Scalar(means);
        output_layers = new ArrayList<>();
        size = new Size(image_size, image_size);
        current_dir = System.getProperty("user.dir");
        model_weights = "Dependencies\\Detect.weights";
        model_config = "Dependencies\\Detect.txt";
        class_file_name_dir = "Dependencies\\Obj_namessss.txt";
        save = true;
    }

    public static int argmax(List<Float> array) {
        float max = array.get(0);
        int re = 0;
        for (int i = 1; i < array.size(); i++) {
            if (array.get(i) > max) {
                max = array.get(i);
                re = i;
            }
        }
        return re;
    }

    public static void setClasses() {
        try {
            File f = new File(class_file_name_dir);
            Scanner reader = new Scanner(f);
            while (reader.hasNextLine()) {
                String class_name = reader.nextLine();
                classes.add(class_name);
            }
        } catch (FileNotFoundException e) {
            errors = true;
        }
    }

    public static void setNetwork() {
        network = Dnn.readNet(model_weights, model_config);
        network = Dnn.readNetFromDarknet(model_config, model_weights);
        //       Dnn.readNet
    }

    public static void setUnconnectedLayers() {
        for (Integer i : network.getUnconnectedOutLayers().toList()) {
            output_layers.add(layer_names.get(i - 1));
        }
    }

    public static void setLayerNames() {
        layer_names = network.getLayerNames();
    }

    public static void loadImage() {
        Mat img = Imgcodecs.imread(input_path);
        Mat resizedImage = new Mat();
        Imgproc.resize(img, resizedImage, size, 0.9, 0.9);
        height = resizedImage.height();
        width = resizedImage.width();
        channels = resizedImage.channels();
        image = resizedImage;
    }

    public static void detectObject() {

        Mat blob_from_image = Dnn.blobFromImage(image, 0.00392, size, mean, true, false);
        network.setInput(blob_from_image);
        outputs = new ArrayList<Mat>();
        network.forward(outputs, output_layers);//
        blob = blob_from_image;
    }

    public static void getBoxDimensions() {
        for (Mat output : outputs) {
            for (int i = 0; i < output.height(); i++) {
                Mat row = output.row(i);
                MatOfFloat temp = new MatOfFloat(row);
                List<Float> detect = temp.toList();
                List<Float> score = detect.subList(5, 85);
                int class_id = argmax(score);
                float conf = score.get(class_id);
                if (conf >= 0.4) {
                    int center_x = (int) (detect.get(0) * width);
                    int center_y = (int) (detect.get(1) * height);
                    int w = (int) (detect.get(2) * width);
                    int h = (int) (detect.get(3) * height);
                    int x = (center_x - w / 2);
                    int y = (center_y - h / 2);
                    Rect2d box = new Rect2d(x, y, w, h);
                    boxes.add(box);
                    confidences.add(conf);
                    class_ids.add(class_id);
                }
            }
        }
    }

    public static void drawLabels() {
        double[] rgb = new double[]{255, 255, 0};//0,255,0-green
        Scalar color = new Scalar(rgb);
        MatOfRect2d mat = new MatOfRect2d();
        mat.fromList(boxes);
        MatOfFloat confidence = new MatOfFloat();
        confidence.fromList(confidences);
        MatOfInt indices = new MatOfInt();
        int font = Imgproc.FONT_HERSHEY_PLAIN;
        Dnn.NMSBoxes(mat, confidence, (float) (0.4), (float) (0.4), indices);
        int aa = 0;
        int no = 0;
        List indices_list = indices.toList();
        for (int i = 0; i < boxes.size(); i++) {
            if (indices_list.contains(i)) {
                if (save) {
                    Rect2d box = boxes.get(i);
                    Point x_y = new Point(box.x, box.y);
                    Point w_h = new Point(box.x + box.width, box.y + box.height);
                    Point text_point = new Point(box.x, box.y - 5);
                    Imgproc.rectangle(image, w_h, x_y, color);
                    String label = classes.get(class_ids.get(i));
                    no++;
                    Float lab = confidences.get(i);
                    String s = Float.toString(lab);
                    String ss = label + s;
                    n++;
                    System.err.println(+no + ". " + label + " = " + lab);///
                    System.err.println(" x:" + box.x + " Width: " + box.width + " Y: " + box.y + " Height:" + box.height);
                    Imgproc.putText(image, ss, text_point, font, 1, color);
                    aa++;
                    String path = "Split_Output";
                    Imgcodecs.imwrite(path + "\\" + aa + ".png", image);
                }
            }
        }
        if (save) {
            Imgcodecs.imwrite(output_path + "\\" + outputFileName + ".png", image);
            t1 = System.currentTimeMillis();
            System.out.println(" Objects detected in " + (t1 - t0) + " milliseconds");
        }
    }

    public static void loadPipeline() {

        Detection.setNetwork();
        Detection.setClasses();
        Detection.setLayerNames();
        Detection.setUnconnectedLayers();
        Detection.loadImage();
        Detection.detectObject();
        Detection.getBoxDimensions();
        Detection.drawLabels();

        errors = true;
    }

}
