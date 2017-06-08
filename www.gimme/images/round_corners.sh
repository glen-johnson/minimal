#!/bin/bash

# need to know the image size!
convert -size 267x178 xc:none -draw "roundrectangle 0,0,267,178,15,15" mask.png
convert crap.jpg -matte mask.png \
  -compose DstIn -composite rounded_corners.png
