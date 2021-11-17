package com.example.mediapipe_android;
import java.util.Calendar;
import android.os.Bundle;
import android.util.Log;
import com.google.mediapipe.framework.PacketGetter;

public class MainActivity extends CameraPreviewActivity {
    private static final String TAG = "HieuNT";
    private static final String OUTPUT_SCORE_STREAM_NAME = "detections";
    private static final String OUTPUT_SCORE_STREAM_TENSOR = "embeddings_tensors";
    private long time= System.currentTimeMillis();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        processor.addPacketCallback(
            OUTPUT_SCORE_STREAM_NAME,
            (packet) -> {
                Log.e(TAG, "has packet");
                Log.e(TAG, "inference time = " + (System.currentTimeMillis() - time));
                time = System.currentTimeMillis();
                float[] scores = PacketGetter.getFloat32Vector(packet);
                int maxindex = 0;
                float max = scores[0];
                String result = "[";
                for(int i=0; i<scores.length;i++) {
//                    Log.e(TAG, "" + scores[i]);
                    result +=  String.valueOf(scores[i]) + ",";
                    if(scores[i] > max){
                        max = scores[i];
                        maxindex = i;
                    }
                }
                result += ']';
                Log.e(TAG, result);
                Log.e(TAG,"index = " + maxindex);
            });
//        processor.addPacketCallback(
//                OUTPUT_SCORE_STREAM_TENSOR,
//                (packet) -> {
//                    Log.e(TAG, "has tesor");
////                    float[] scores = PacketGetter.getFloat32Vector(packet);
//                });
    }
}