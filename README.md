<pre>

Usage: slimapk [options...] <&ampINPUT_FOLDER/FILE> <&ampOUTPUT_FOLDER>

options:
 -a32 (--arm) - ARM [default]
 -a64 (--arm64) - ARM 64-bit
 -x86 (--x86) - X86
 -k (--keep-directory) keep the structure the f/s
 -l [-l="path to save the list of applications"]
 -i (--info) get more information about a specific package

</pre>

Example: 

$ java -jar slimapk.jar -a32 -k apps/ out/ -l=list.txt

$ java -jar slimapk.jar antox.apk -i

<pre>

[INFO] Package: chat.tox.antox
[INFO] Name: Antox
[INFO] Version: 0.23.54
[INFO] VersionCode: 1554
[INFO] minSdkVersion: 9
[INFO] native-code: false
[INFO] Library: armeabi

</pre>
