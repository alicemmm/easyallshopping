# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# Reference：http://proguard.sourceforge.net/#manual/usage.html、http://www.cnblogs.com/renkangke/archive/2013/05/31/3110635.html
# 下列情况不能使用混淆：
# 1.在AndroidManifest中配置的类，比如四大组件
# 2.JNI调用的方法
# 3.反射用到的类
# 4.WebView中JavaScript调用的方法
# 5.Layout文件引用到的自定义View
# 6.一些引入的第三方库（一般都会有混淆说明的）

# Keep crash stack info
# -renamesourcefileattribute Proguard

#不忽略指定jars中的非public calsses （默认选项）
-dontskipnonpubliclibraryclasses
#混淆过程中打印详细信息，如果异常终止则打印整个堆栈信息
-verbose
#打印旧名称到重命名的类、类成员的新名称的映射关系，可输出到指定文件。
-printmapping proguardMapping.txt
-keepattributes *Annotation*,InnerClasses
-keepattributes Signature

-keepattributes SourceFile, LineNumberTable
-keepattributes EnclosingMethod
-ignorewarnings

#指定压缩级别
-optimizationpasses 5

#不跳过非公共的库的类成员
-dontskipnonpubliclibraryclassmembers

#混淆时采用的算法
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

#把混淆类中的方法名也混淆了
-useuniqueclassmembernames

#优化时允许访问并修改有修饰符的类和类的成员
-allowaccessmodification

#将文件来源重命名为“SourceFile”字符串
-renamesourcefileattribute SourceFile
#保留行号
-keepattributes SourceFile,LineNumberTable

#agentweb
-keep class com.just.library.** {
    *;
}
-dontwarn com.just.library.**
-keepclassmembers class com.just.library.agentweb.AndroidInterface{ *; }

-keep class cn.bmob.v3.** {*;}
#-keep class com.example.bmobexample.bean.BankCard{*;}
