# Detection with pytorch mobile
*Probably first open-source mobile detection on pytorch*

[Description](#description)  
[Why it's low-accuracy?](#why-its-low-accuracy)  
[TODO](#todo)  
[Links](#links)

## Description
Most of the [detection ops](https://pytorch.org/docs/stable/torchvision/ops.html) not implemented in pytorch mobile. 
But it's possible to implement post-processing ops, like nms, and apply them.

<img src="https://raw.githubusercontent.com/temirgaliyev/pytorch_mobile_detection/master/static/pytorch_detection_cats.png" width=30% height=30%>


## Why it's low accuracy?
I'm not sure. I noticed that model has slightly different outputs on Android and Python.  
I think, it might be quantization issue (double→float) or differences of implementations.

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

## Links
[Pytorch: Pytorch Mobile](https://pytorch.org/mobile/android/)  
[Github: PyTorch Android Examples](https://github.com/pytorch/android-demo-app)  
[Github: Pytorch SSD](https://github.com/qfgaohao/pytorch-ssd)
