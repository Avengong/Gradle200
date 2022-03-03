https://zhuanlan.zhihu.com/p/449012470

# 疑问

1 app可以看做一个project工程，module可以看做一个工程吗？

2 当在app和module目录中的build.gradle文件中获取到的rootdir是什么？projectDir又是什么？ 答：
rootDir指的是项目根目录。projectDir指的是当前project的路径，比如是app，则表示是app当前路径，如果是module则是当前module的路径。 例子：

```
=====module==projectDir:/Users/avengong/Desktop/demos/Gradle200/gradle-one,rootDir:/Users/avengong/Desktop/demos/Gradle200 

=====app==projectDir:/Users/avengong/Desktop/demos/Gradle200/chapter01/sample01,rootDir:/Users/avengong/Desktop/demos/Gradle200 



```

3, rootProject.name 和 project.name的区别 rootProject.name指的是跟工程的名字，可在setting中修改。
project.name如果是app则是app的名字， 如果在module中则是module的名字。 这个功能可用来定位查找打包的aar产物的名字

```
=====app1==projectDir:/Users/avengong/Desktop/demos/Gradle200/app,rootDir:/Users/avengong/Desktop/demos/Gradle200，rootProjectNmae: Gradle777 project.name: app

=====module==projectDir:/Users/avengong/Desktop/demos/Gradle200/gradle-one,rootDir:/Users/avengong/Desktop/demos/Gradle200，rootProjectNmae: Gradle777 project.name: gradle-one

=====app2==projectDir:/Users/avengong/Desktop/demos/Gradle200/chapter01/sample01,
rootDir:/Users/avengong/Desktop/demos/Gradle200，rootProjectNmae: Gradle777 project.name: sample01

```








