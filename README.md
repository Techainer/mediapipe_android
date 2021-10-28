# Mediapipe Android 
- This repo is a demo mediapipe graph in android project.
## Install and running
- Clone this repo 
```
https://github.com/Techainer/mediapipe_android.git
```
- Run by android studio

## Building own customize graph 
### Clone mediapipe
First, we need to clone the mediapipe. Then use this framework to build graphs, android jar file and shared library.
```
git clone https://github.com/google/mediapipe.git
```
### Docker
- For the convenience of setting up the environment, you should use docker.
```
cd mediapipe
docker build --tag=mediapipe .
docker run -it --name mediapipe mediapipe:latest
```

- When enter docker again:
```
docker start [your container ID] 
docker exec -i -t mediapipe bash
```

Run below commands on the container.

### Setup
```
apt update
apt install nano
apt install zip
bash ./setup_android_sdk_and_ndk.sh
```

### Prepare
- Add env variable 
To easily work with mediapipe, we need to name the project, graph according to a rule.
We will use environment variables. Change project_name to your project name.
```
export project_name="flutter_mediapipe"
```

- "BUILD" file.
```
mkdir mediapipe/examples/android/src/java/com/google/mediapipe/apps/${project_name}
nano mediapipe/examples/android/src/java/com/google/mediapipe/apps/${project_name}/BUILD 
```

- "BUILD" file content.
change project_name to your project name
```
load("//mediapipe/java/com/google/mediapipe:mediapipe_aar.bzl", "mediapipe_aar")
mediapipe_aar(
    name = "{{ project_name }}",
    calculators = ["//mediapipe/graphs/{{ project_name }}:mobile_calculators"],
)
```

### GRAPH
- Graph "BUILD" file.
```
mkdir mediapipe/mediapipe/graphs/${project_name}
nano mediapipe/mediapipe/graphs/${project_name}/BUILD
```
- Graph "BUILD" file content.
change project_name by your project name
```
licenses(["notice"])
package(default_visibility = ["//visibility:public"])
load(
    "//mediapipe/framework/tool:mediapipe_graph.bzl",
    "mediapipe_binary_graph",
)

cc_library(
    name = "mobile_calculators",
    deps = [
        "//mediapipe/calculators/core:flow_limiter_calculator",
        "//mediapipe/calculators/image:image_transformation_calculator",
        "//mediapipe/calculators/tflite:tflite_converter_calculator",
        "//mediapipe/calculators/tflite:tflite_inference_calculator",
        "//mediapipe/calculators/tflite:tflite_tensors_to_floats_calculator",
        "//mediapipe/gpu:gpu_buffer_to_image_frame_calculator",
        "//mediapipe/gpu:image_frame_to_gpu_buffer_calculator",
        "//mediapipe/calculators/tensor:image_to_tensor_calculator",
        "//mediapipe/calculators/util:to_image_calculator",
    ],
)

mediapipe_binary_graph(
    name = "{{ project_name }}",
    graph = "{{ project_name }}.pbtxt",
    output_name = "{{ project_name }}.binarypb",
)
```

- Graph file
```
nano mediapipe/examples/android/src/java/com/google/mediapipe/apps/${project_name}/${project_name}.pbtxt
```

- Graph file content
```
# Define input, ouput
input_stream: "input_frames"
output_stream: "output_frames"
output_stream: "scores_list"

# Graph pipeline
node {
  calculator: "FlowLimiterCalculator"
  input_stream: "input_frames"
  input_stream: "FINISHED:output_frames"
  input_stream_info: {
    tag_index: "FINISHED"
    back_edge: true
  }
  output_stream: "output_frames"
}

node: {
  calculator: "ImageTransformationCalculator"
  input_stream: "IMAGE_GPU:output_frames"
  output_stream: "IMAGE_GPU:transformed_frame"
  node_options: {
    [type.googleapis.com/mediapipe.ImageTransformationCalculatorOptions]: {
      flip_horizontally: true
    }
  }
}

node {
  calculator: "GpuBufferToImageFrameCalculator"
  input_stream: "transformed_frame"
  output_stream: "scaled_head_cpu"
}

node {
  calculator: "TfLiteConverterCalculator"
  input_stream: "IMAGE:scaled_head_cpu"
  output_stream: "TENSORS:scaled_head_tensor"
}

node {
  calculator: "TfLiteInferenceCalculator"
  input_stream: "TENSORS:scaled_head_tensor"
  output_stream: "TENSORS:embeddings_tensors"
  node_options: {
    [type.googleapis.com/mediapipe.TfLiteInferenceCalculatorOptions] {
      use_gpu: false
      model_path: "mediapipe/models/Faceliveness_mobilenetv2_model6_20211006.tflite.tflite"
    }
  }
}

node {
  calculator: "TfLiteTensorsToFloatsCalculator"
  input_stream: "TENSORS:embeddings_tensors"
  output_stream: "FLOATS:scores_list"
}
```

### Build
- jniLibs
```
bazel build -c opt --host_crosstool_top=@bazel_tools//tools/cpp:toolchain --fat_apk_cpu=arm64-v8a,armeabi-v7a --strip=ALWAYS //mediapipe/examples/android/src/java/com/google/mediapipe/apps/${project_name}:BUILD --linkopt="-s"
bazel build -c opt --host_crosstool_top=@bazel_tools//tools/cpp:toolchain --fat_apk_cpu=arm64-v8a,armeabi-v7a //mediapipe/examples/android/src/java/com/google/mediapipe/apps/${project_name}:${project_name} --linkopt="-s"
```
- binary graph
```
bazel build -c opt mediapipe/graphs/${project_name}:${project_name}
```

### mkdir
```
mkdir ${project_name}/
mkdir ${project_name}/android
mkdir ${project_name}/android/libs
mkdir ${project_name}/android/src
mkdir ${project_name}/android/src/main
mkdir ${project_name}/android/src/main/assets
mkdir ${project_name}/android/src/main/jniLibs
mkdir ${project_name}/protos
```

### libs
```
cp bazel-bin/mediapipe/examples/android/src/java/com/google/mediapipe/apps/${project_name}/lib${project_name}_android_lib.jar ${project_name}/android/libs
```

### assets
```
cp bazel-out/k8-opt/bin/mediapipe/graphs/${project_name}/${project_name}.binarypb ${project_name}/android/src/main/assets
```

### jniLibs

```
mkdir work_${project_name}
cp bazel-bin/mediapipe/examples/android/src/java/com/google/mediapipe/apps/${project_name}/${project_name}.aar work/aar.zip
cd work_${project_name}/
unzip aar.zip
cd ..
cp -r work_${project_name}/jni/* flutter_mediapipe/android/src/main/jniLibs/
```

### ZIP 
```
zip -r flutter_mediapipe.zip flutter_mediapipe
```

### COPY 
change flutter_mediapipe to your {{ project_name }}
```
docker cp mediapipe:/mediapipe/flutter_mediapipe.zip .
unzip flutter_mediapipe.zip
```

## Integrating 
- Copy libs file.jar to this repo libs
- Copy jnib to jnib to this android repo
- Copy binary graph to this repo asset 
- Copy AI model to this repo asset 
- Change graph name in CameraActivity.java 

## reference
- https://pub.dev/packages/flutter_mediapipe 
- https://google.github.io/mediapipe/getting_started/hello_world_android.html
- https://google.github.io/mediapipe/getting_started/android_archive_library.html

