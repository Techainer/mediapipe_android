package com.example.mediapipe_android;

import android.os.Bundle;
import android.util.Log;

import com.google.mediapipe.formats.proto.DetectionProto;
import com.google.mediapipe.formats.proto.LandmarkProto.NormalizedLandmark;
import com.google.mediapipe.formats.proto.LandmarkProto.NormalizedLandmarkList;
import com.google.mediapipe.framework.AndroidPacketCreator;
import com.google.mediapipe.framework.Packet;
import com.google.mediapipe.framework.PacketGetter;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.mediapipe.formats.proto.DetectionProto.DetectionList;
import com.google.mediapipe.formats.proto.DetectionProto.Detection;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.security.auth.login.LoginException;

public class MainActivity extends CameraPreviewActivity {
//    private static final String TAG = "MainActivity";
    private static final String TAG = "HieuNT";

    private static final String INPUT_NUM_FACES_SIDE_PACKET_NAME = "num_faces";
//    private static final String OUTPUT_LANDMARKS_STREAM_NAME = "multi_face_landmarks";
//    private static final String OUTPUT_LANDMARKS_STREAM_NAME = "face_detections";
    private static final String OUTPUT_LANDMARKS_STREAM_NAME = "scores_list";


    // Max number of faces to detect/process.
    private static final int NUM_FACES = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        AndroidPacketCreator packetCreator = processor.getPacketCreator();
//        Map<String, Packet> inputSidePackets = new HashMap<>();
//        inputSidePackets.put(INPUT_NUM_FACES_SIDE_PACKET_NAME, packetCreator.createInt32(NUM_FACES));
//        processor.setInputSidePackets(inputSidePackets);

        // To show verbose logging, run:
        // adb shell setprop log.tag.MainActivity VERBOSE
        Log.e("HieuNT","Chuan bi add callback processor");
        if (true) {
            Log.e("HieuNT","Chuan bi add callback processor");
            processor.addPacketCallback(
                    OUTPUT_LANDMARKS_STREAM_NAME,
                    (packet) -> {
                        Log.e(TAG,"hass packettttttt");
//                        byte[] detectionsRaw = PacketGetter.getProtoBytes(packet);
//                        try {
//                            DetectionList detections = DetectionList.parseFrom(detectionsRaw);
//                            if (detections == null) {
//                                Log.d(TAG, "[TS:" + packet.getTimestamp() + "] No face detections.");
//                                return;
//                            }
//                            // Note: If hand_presence is false, these landmarks are useless.
//                            Log.d(
//                                    TAG,
//                                    "[TS:"
//                                            + packet.getTimestamp()
//                                            + "] #Landmarks for face: ");
//                        } catch (InvalidProtocolBufferException e) {
//                            Log.e(TAG, "Couldn't Exception received - " + e);
//                            return;
//                        }

//                        List<NormalizedLandmarkList> multiFaceLandmarks =
//                                PacketGetter.getProtoVector(packet, NormalizedLandmarkList.parser());

                        List<Detection> detections = PacketGetter.getProtoVector(packet, Detection.parser());
                        Log.e(TAG, "face len = " + detections.size());
                        if (detections == null) {
                            Log.d(TAG, "[TS:" + packet.getTimestamp() + "] No face detections.");
                            return;
                        }
                        int detectionIndex = 0;
                        String detectionsString = "hhhh ";
                        for( int i=0 ; i<detections.size();i++){
                            Log.e(TAG,"Xet i = " + i);
                           Detection detec = detections.get(i);
                                detectionsString +=
                                        "\t\tdetection["
                                                + detectionIndex
                                                + "]: ("
//                                                + "feature-tag: "
//                                                + detec.getFeatureTag()
//                                                + "confidence: "
//                                                + detec.getAssociatedDetections(0).getConfidence()
                                                + ",width: "
                                                + detec.getLocationData().getRelativeBoundingBox().getWidth()
                                                + ", height: "
                                                + detec.getLocationData().getRelativeBoundingBox().getHeight()
                                                + ", left: "
                                                + detec.getLocationData().getRelativeBoundingBox().getXmin()
                                                + ", top: "
                                                + detec.getLocationData().getRelativeBoundingBox().getYmin()
                                                + ")\n";
                                ++detectionIndex;
                        }

//                        Log.e(TAG,detectionsString);

//                        Log.e(TAG, "face = " + detections.get(0));
//
//                        // PacketGetter.ge
//                        Log.e(
//                                TAG,
//                                "[TS:"
//                                        + packet.getTimestamp()
//                                        + "] "
//                                        + getMultiFaceLandmarksDebugString(multiFaceLandmarks));
                    });
        }
        else{
            Log.v(TAG,"Deo add duoc");
        }
    }


    private static String getDetectionsDebugString(DetectionList detections) {
        int detectionIndex = 0;
        String detectionsString = "";
        System.out.println(detections.getDetectionCount());
        for (Detection detection : detections.getDetectionList()) {
            if (detection.hasDetectionId() && detection.getAssociatedDetections(0).getConfidence() > 0.5) {
                detectionsString +=
                        "\t\tdetection["
                                + detectionIndex
                                + "]: ("
                                + "feature-tag: "
                                + detection.getFeatureTag()
                                + "confidence: "
                                + detection.getAssociatedDetections(0).getConfidence()
                                + ",width: "
                                + detection.getLocationData().getRelativeBoundingBox().getWidth()
                                + ", height: "
                                + detection.getLocationData().getRelativeBoundingBox().getHeight()
                                + ", left: "
                                + detection.getLocationData().getRelativeBoundingBox().getXmin()
                                + ", top: "
                                + detection.getLocationData().getRelativeBoundingBox().getYmin()
                                + ")\n";
                ++detectionIndex;
            }
        }
        return detectionsString;
    }

    private static String getMultiFaceLandmarksDebugString(
            List<NormalizedLandmarkList> multiFaceLandmarks) {
        if (multiFaceLandmarks.isEmpty()) {
            return "No face landmarks";
        }
        String multiFaceLandmarksStr = "Number of faces detected: " + multiFaceLandmarks.size() + "\n";
        int faceIndex = 0;
        for (NormalizedLandmarkList landmarks : multiFaceLandmarks) {
            multiFaceLandmarksStr +=
                    "\t#Face landmarks for face[" + faceIndex + "]: " + landmarks.getLandmarkCount() + "\n";
            int landmarkIndex = 0;
            for (NormalizedLandmark landmark : landmarks.getLandmarkList()) {
                multiFaceLandmarksStr +=
                        "\t\tLandmark ["
                                + landmarkIndex
                                + "]: ("
                                + landmark.getX()
                                + ", "
                                + landmark.getY()
                                + ", "
                                + landmark.getZ()
                                + ")\n";
                ++landmarkIndex;
            }
            ++faceIndex;
        }
        return multiFaceLandmarksStr;
    }
}

//
//package com.example.mediapipe_android;
//
//import androidx.appcompat.app.AppCompatActivity;
//import android.os.Bundle;
//
//
//import android.content.pm.ApplicationInfo;
//import android.content.pm.PackageManager;
//import android.content.pm.PackageManager.NameNotFoundException;
//import android.graphics.SurfaceTexture;
//import android.os.Bundle;
//import androidx.appcompat.app.AppCompatActivity;
//import android.util.Log;
//import android.util.Size;
//import android.view.SurfaceHolder;
//import android.view.SurfaceView;
//import android.view.View;
//import android.view.ViewGroup;
//import com.google.mediapipe.components.CameraHelper;
//import com.google.mediapipe.components.CameraXPreviewHelper;
//import com.google.mediapipe.components.ExternalTextureConverter;
//import com.google.mediapipe.components.FrameProcessor;
//import com.google.mediapipe.components.PermissionHelper;
//import com.google.mediapipe.framework.AndroidAssetUtil;
//import com.google.mediapipe.glutil.EglManager;
//
//public class MainActivity extends AppCompatActivity {
//    private static final String TAG = "MainActivity";
//
//    private static final boolean FLIP_FRAMES_VERTICALLY = true;
//
//    private static final int NUM_BUFFERS = 2;
//    protected FrameProcessor processor;
//    protected CameraXPreviewHelper cameraHelper;
//    private SurfaceTexture previewFrameTexture;
//    private SurfaceView previewDisplayView;
//    private EglManager eglManager;
//    private ExternalTextureConverter converter;
//    private ApplicationInfo applicationInfo;
//
//    //    private static final String BINARY_GRAPH_NAME = "face_detection_mobile_gpu.binarypb";
//    private static final String BINARY_GRAPH_NAME = "card_liveness_graph.binarypb";
//    //    private static final String BINARY_GRAPH_NAME = "face_mesh_mobile_gpu.binarypb";
//    private static final String INPUT_VIDEO_STREAM_NAME = "input_frames";
//    private static final String OUTPUT_VIDEO_STREAM_NAME = "output_frames";
//
//
//    static {
//        System.loadLibrary("mediapipe_jni");
//        try {
//            System.loadLibrary("opencv_java3");
//        } catch (java.lang.UnsatisfiedLinkError e) {
//            System.loadLibrary("opencv_java4");
//        }
//    }
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(getContentViewLayoutResId());
//
//        try {
//            applicationInfo =
//                    getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
//        } catch (NameNotFoundException e) {
//            Log.e(TAG, "Cannot find application info: " + e);
//        }
//
//        previewDisplayView = new SurfaceView(this);
//        setupPreviewDisplayView();
//
//        AndroidAssetUtil.initializeNativeAssetManager(this);
//        eglManager = new EglManager(null);
//        Log.e("HieuNT","Load binary map");
//        processor = new FrameProcessor(this, eglManager.getNativeContext(), BINARY_GRAPH_NAME,
//                INPUT_VIDEO_STREAM_NAME, OUTPUT_VIDEO_STREAM_NAME);
//        Log.e("HieuNT","Load done");
//
//        processor
//                .getVideoSurfaceOutput()
//                .setFlipY(FLIP_FRAMES_VERTICALLY);
//
//        PermissionHelper.checkAndRequestCameraPermissions(this);
//    }
//    protected int getContentViewLayoutResId() {
//        return R.layout.activity_main;
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        converter =
//                new ExternalTextureConverter(
//                        eglManager.getContext());
//        converter.setFlipY(FLIP_FRAMES_VERTICALLY);
//        converter.setConsumer(processor);
//        if (PermissionHelper.cameraPermissionsGranted(this)) {
//            startCamera();
//        }
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        converter.close();
//
//        // Hide preview display until we re-open the camera again.
//        previewDisplayView.setVisibility(View.GONE);
//    }
//
//    @Override
//    public void onRequestPermissionsResult(
//            int requestCode, String[] permissions, int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        PermissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
//    }
//
//    protected void onCameraStarted(SurfaceTexture surfaceTexture) {
//        previewFrameTexture = surfaceTexture;
//        previewDisplayView.setVisibility(View.VISIBLE);
//    }
//
//    protected Size cameraTargetResolution() {
//        return null; // No preference and let the camera (helper) decide.
//    }
//
//    public void startCamera() {
//        cameraHelper = new CameraXPreviewHelper();
//        cameraHelper.setOnCameraStartedListener(
//                surfaceTexture -> {
//                    onCameraStarted(surfaceTexture);
//                });
//        CameraHelper.CameraFacing cameraFacing =  CameraHelper.CameraFacing.FRONT;
//        cameraHelper.startCamera(
//                this, cameraFacing, /*unusedSurfaceTexture=*/ null, cameraTargetResolution());
//    }
//
//    protected Size computeViewSize(int width, int height) {
//        return new Size(width, height);
//    }
//
//    protected void onPreviewDisplaySurfaceChanged(
//            SurfaceHolder holder, int format, int width, int height) {
//        Log.e("HieuNT","surface changed");
//        Size viewSize = computeViewSize(width, height);
//        Size displaySize = cameraHelper.computeDisplaySizeFromViewSize(viewSize);
//        boolean isCameraRotated = cameraHelper.isCameraRotated();
//        converter.setSurfaceTextureAndAttachToGLContext(
//                previewFrameTexture,
//                isCameraRotated ? displaySize.getHeight() : displaySize.getWidth(),
//                isCameraRotated ? displaySize.getWidth() : displaySize.getHeight());
//    }
//
//    private void setupPreviewDisplayView() {
//        Log.e("HieuNT","Set up PreviewDisplayView");
//        previewDisplayView.setVisibility(View.GONE);
//        ViewGroup viewGroup = findViewById(R.id.preview_display_layout);
//        viewGroup.addView(previewDisplayView);
//
//        previewDisplayView
//                .getHolder()
//                .addCallback(
//                        new SurfaceHolder.Callback() {
//                            @Override
//                            public void surfaceCreated(SurfaceHolder holder) {
//                                Log.e("HieuNT","surface created");
//                                processor.getVideoSurfaceOutput().setSurface(holder.getSurface());
//                            }
//                            @Override
//                            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
//                                onPreviewDisplaySurfaceChanged(holder, format, width, height);
//                            }
//                            @Override
//                            public void surfaceDestroyed(SurfaceHolder holder) {
//                                Log.e("HieuNT","surface Destroyed");
//                                processor.getVideoSurfaceOutput().setSurface(null);
//                            }
//                        });
//    }
//}