<pre>

Usage: slimapk [options...] <&ampINPUT_FOLDER/FILE> <&ampOUTPUT_FOLDER>

options:
 -a32 (--arm) - ARM [default]
 -a64 (--arm64) - ARM 64-bit
 -x86 (--x86) - X86
 -k (--keep-directory) keep the structure the f/s
 -p [-p="template for the file name"]
 -l [-l="path to save the list of applications"]
 -i (--info) get more information about a specific package

</pre>

Example: 

$ java -jar slimapk.jar -a32 -k apps/ out/ -l=list.txt

$ java -jar slimapk.jar VLC-Android-ARMv7.apk -i

<pre>

[INFO] PackageName: VLC
[INFO] Version: 1.4.1
[INFO] minSdkVersion: 7

</pre>
