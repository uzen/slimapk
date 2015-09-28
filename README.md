<pre>

Usage: slimapk [options...] <&ampINPUT_FOLDER/FILE> <&ampOUTPUT_FOLDER>

options:
 -a32 (--arm) - ARM
 -a64 (--arm64) - ARM 64-bit
 -x86 (--x86) - X86
 -x86_64 (--x86_64) - X86_64
 -m (--multiple) : ARM, ARM64, X86, X86_64
 -k (--keep-directory) keep the structure the f/s
 -l [-l="path to save the list of applications"]
 -i (--info) get more information about a specific package
 -ec (--enable-catch) working with a copy of the application from the temporary folder


</pre>

Example: 

$ java -jar slimapk.jar -a32 -k apps/ out/ -l=list.txt

$ java -jar slimapk.jar com.bittorrent.chat.apk -i

<pre>

[INFO] Package: 	com.bittorrent.chat
[INFO] PackageName: 	Bleep
[INFO] Version: 	1.0.616
[INFO] VersionCode: 	616
[INFO] sdkVersion: 	22
[INFO] minSdkVersion: 	14
[INFO] native-code: 	x86 armeabi

</pre>
