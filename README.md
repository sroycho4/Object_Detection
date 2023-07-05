# Object_Detection

# Object Detection Project

This project utilizes OpenCV 4.0.1 and YOLO v3 for object detection.

## Prerequisites

Before running the project, make sure you have the following dependencies installed:

1. OpenCV 4.0.1: Download and install OpenCV 4.0.1 from the following link:
   - [OpenCV 4.0.1 Download](https://sourceforge.net/projects/opencvlibrary/files/4.0.1/opencv-4.0.1-vc14_vc15.exe/download)

2. YOLO v3 Model Files: Download the YOLO v3 model files from the following link:
   - [YOLO v3 Model Files Download](https://pjreddie.com/darknet/yolo/)

## Project Setup

Follow these steps to set up the project:

1. Create a new folder named "Dependencies" in the project directory.

2. Extract the downloaded OpenCV 4.0.1 files and copy them into the "Dependencies" folder.

3. Copy the YOLO v3 model files (`model_weights`, `model_config`, `class_file_name_dir`) into the "Dependencies" folder.

## Running the Project

1. Open the project in your preferred Java IDE.

2. Locate the `ObjectDetection` class.

3. Run the `ObjectDetection` class to launch the application.

4. Click on the "Get Dataset Image" button to select an input image for object detection.

5. Click on the "Detect Dataset Image" button to perform object detection on the selected image.

6. The detected image will be displayed in the application window.

## Notes

- Make sure to set the correct path to the YOLO v3 model files in the `Detection` class (`model_weights`, `model_config`, `class_file_name_dir`) if you place them in a different location.

- The output image will be saved in the "Output" folder as "Object_Detected.png".

- The project assumes a specific image size of 416x416 for detection. If you want to change the image size, modify the `image_size` parameter when creating a `Detection` object.

- The detected objects will be labeled with their class names and confidence scores.

- The processing time for object detection will be displayed in the console.

That's it! You have successfully set up and run the Object Detection project using OpenCV and YOLO v3.
