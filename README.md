# Detection with pytorch mobile
*Probably first open-source mobile detection on pytorch*  
Let me know in issues if it's not correct  
  
[Description](#description)  
[Why it's low-accuracy?](#why-ssd-has-low-accuracy)  
[TODO](#todo)  
[Links](#links)

## Description
Most of the [detection ops](https://pytorch.org/docs/stable/torchvision/ops.html) not implemented in pytorch mobile. 
But it's still possible to implement post-processing ops, like nms, and apply them.

<img src="https://raw.githubusercontent.com/temirgaliyev/pytorch_mobile_detection/master/static/pytorch_detection_cats.png" width=30% height=30%>


## Why SSD has low accuracy?
Checked preprocessing and made so that input tensors in python and java was exactly same. But outputs stays different. Probably, something wrong with flow of tensor of traced SSD model on torch mobile. 

## TODO
- [x] Take image from gallery
- [x] Implement transformations
  - [x] Resize
  - [x] Normalization
- [x] Implement post-processing
  - [x] Softmax
  - [x] Locations to boxes convertation
  - [x] Non-maximum suppression
- [x] Draw boxes on image
- [ ] Real-time object detection
- [ ] Upload models weights to storage and write downloading script
- [ ] Quantize models (now DETR model params weights 158MB)

## Links
[Pytorch: Pytorch Mobile](https://pytorch.org/mobile/android/)  
[Github: PyTorch Android Examples](https://github.com/pytorch/android-demo-app)  
[Github: Pytorch SSD](https://github.com/qfgaohao/pytorch-ssd)  
[Github: Android Camera2 API Example](https://github.com/Jiankai-Sun/Android-Camera2-API-Example)
