package com.example.mediapipe_android;

import android.os.Bundle;
import android.util.Log;
import com.google.mediapipe.framework.PacketGetter;

public class MainActivity extends CameraPreviewActivity {
    private static final String TAG = "HieuNT";
    private static final String OUTPUT_SCORE_STREAM_NAME = "scores_list";
    private static final String OUTPUT_SCORE_STREAM_TENSOR = "embeddings_tensors";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        processor.addPacketCallback(
            OUTPUT_SCORE_STREAM_NAME,
            (packet) -> {
                Log.e(TAG, "has packet");
                float[] scores = PacketGetter.getFloat32Vector(packet);
                String result = "[";
                for(int i=0; i<scores.length;i++) {
                    result +=  String.valueOf(scores[i]) + ",";
                }
                result += ']';
                Log.e(TAG, result);
            });
        processor.addPacketCallback(
                OUTPUT_SCORE_STREAM_TENSOR,
                (packet) -> {
                    Log.e(TAG, "has tesor");
//                    float[] scores = PacketGetter.getFloat32Vector(packet);
                });
    }
}