package ch.hslu.structhing.backend.ai;

import ai.djl.Application;
import ai.djl.ModelException;
import ai.djl.engine.Engine;
import ai.djl.inference.Predictor;
import ai.djl.modality.Classifications;
import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.ImageFactory;
import ai.djl.modality.cv.output.DetectedObjects;
import ai.djl.modality.cv.transform.CenterCrop;
import ai.djl.modality.cv.transform.Normalize;
import ai.djl.modality.cv.transform.Resize;
import ai.djl.modality.cv.transform.ToTensor;
import ai.djl.modality.cv.translator.ImageClassificationTranslator;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ModelLoader;
import ai.djl.repository.zoo.ModelZoo;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.training.util.DownloadUtils;
import ai.djl.training.util.ProgressBar;
import ai.djl.translate.TranslateException;
import ai.djl.translate.Translator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.spi.ImageReaderWriterSpi;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ExampleDJL {

    public static void main(String[] args) throws IOException, ModelException, TranslateException {
        String imagePath = "/home/lordnik/Documents/structhing/backend/src/main/resources/test.jpg";
        Path image = Path.of(imagePath);

        System.out.println("Does my image exists?: " + Files.exists(image));

        ZooModel<Image, Classifications> model = loadModel();
        Predictor<Image, Classifications> predictor = model.newPredictor();


        BufferedImage bufferedImage = ImageIO.read(Path.of(imagePath).toFile());
        ImageFactory factory = ImageFactory.getInstance();
        Image img = factory.fromImage(bufferedImage);
        Classifications detections = predictor.predict(img);
//
//        img.drawBoundingBoxes(detections);
//        img.save(new FileOutputStream(Path.of("src/main/resources/test-with-border.jpg").toFile()), "jpeg");
        predictor.close();
        model.close();
    }

//    private static Mat readImage(String imagePath) {
//        try {
//            // Read the image as a BufferedImage
//            BufferedImage bufferedImage = ImageIO.read(Path.of(imagePath).toFile());
//            // Convert BufferedImage to Mat
//            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//            ImageIO.write(bufferedImage, "jpg", byteArrayOutputStream);
//            Mat imageMat = Imgcodecs.imdecode(new MatOfByte(byteArrayOutputStream.toByteArray()), Imgcodecs.IMREAD_UNCHANGED);
//            // Convert to a 3-channel color image if it's grayscale
//            if (imageMat.channels() == 1) {
//                Imgproc.cvtColor(imageMat, imageMat, Imgproc.COLOR_GRAY2BGR);
//            }
//
//            return imageMat;
//        } catch (IOException e) {
//            e.printStackTrace();
//            return new Mat();
//        }
//    }

    private static ZooModel<Image, Classifications> loadModel() throws IOException, ModelException {
        Criteria<Image, Classifications> criteria = Criteria.builder()
                .setTypes(Image.class, Classifications.class)
                .optApplication(Application.CV.IMAGE_CLASSIFICATION)
                .optGroupId("ai.djl.zoo")
                .optArtifactId("resnet")
                .optProgress(new ProgressBar())
                .build();
       return criteria.loadModel();
    }

//    private static BufferedImage toBufferedImage(Mat mat) {
//        int width = mat.width();
//        int height = mat.height();
//        int type = mat.channels() != 1 ? BufferedImage.TYPE_3BYTE_BGR : BufferedImage.TYPE_BYTE_GRAY;
//
//        if (type == BufferedImage.TYPE_3BYTE_BGR) {
//            Imgproc.cvtColor(mat, mat, Imgproc.COLOR_BGR2RGB);
//        }
//
//        byte[] data = new byte[width * height * (int) mat.elemSize()];
//        mat.get(0, 0, data);
//
//        BufferedImage ret = new BufferedImage(width, height, type);
//        ret.getRaster().setDataElements(0, 0, width, height, data);
//
//        return ret;
//    }
}
