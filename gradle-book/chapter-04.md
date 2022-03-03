# 第四章 Gradle 任务

gradle所有的构建工作都是有tasks来组合完成，因此，task-任务，尤其重要。

## 4.1 多种方式创建任务

为什么会有多种方式呢？ 得益于Porject给我们提供的方法和taskContainer提供的create方法。

1. 直接以任务名字来定义

```
def Task exeCreateTask0=task("exeCreateTask0")

exeCreateTask0.doLast{

}
```

2. 通过任务名+map参数配置来定义任务

```
def Task exeT1=task("exeT1",group: BasePlugin.BUILD_GROUP)
exeT1.doLast {
    println " name: ${exeT1.name}, group:${exeT1.group}"
}
```

map可配置字段有： description： 描述信息 group 分组 dependsOn 依赖某个任务执行

3. 第三种方式为 任务名字+闭包配置

```
task exe2{
    description "abcaaaaaaaaa"
    dependsOn exeCreateTask0
    group BasePlugin.BUILD_GROUP
    doLast {
        println "exe2: dolast! "
    }
}
```

4. 第四种通过tasksContainer的create方法

```
//方式四
tasks.create("exe4"){

    description "vvvvvvvvvvvvvvvv"
    dependsOn exeCreateTask0
    group BasePlugin.BUILD_GROUP
    doLast {
        println "create: dolast! description:${description}"
    }
}
```

## 4.2 多种方式访问任务

我们知道任务创建后，其本质上就是项目(Project)中的一个属性。属性名就是任务名，所以我们可以通过任务名来操作任务。

1. 直接通过任务对象来操作

```
task exeTask
exeTask.dolast{
}


```

2. 通过taskContainer访问

```
//访问  好骚啊  这个操作
tasks["exe4"].doFirst {
    println "exe4: doFirst-----! description:${description}"
}
```

3. 通过路径访问

```
//通过taskscontainer
tasks["exe4"].doFirst {
    println "exe4: doFirst-----! description:${description}"

    //4 . 通过路径
    println  tasks.findByPath(":app:exe2")
    println  tasks.getByPath("exe2")
    println  tasks.findByPath("exe244") //找不到返回null
//    println  tasks.getByPath("exe244") //找不到会报错

    //5 . 通过名字
    println  tasks.getByName("exeCreateTask0")
    println  tasks.findByName("exe2")
    println  tasks.findByName("exe442")

}


```

4. 通过名字访问

总结： 拿到该任务对象后，我们就可以按照我们的逻辑去操作它啦！！！

## 4.3 任务的分组和描述

通过group和description来控制，简单。

## 4.4 << 操作符

报错了，废弃啦？？？ 没什么卵用啊

## 4.5 任务的执行分析

Task本质上是有一个actions列表来完成的。 dofirst和dolast会被作为action加载到列表的头部和尾部。而task本身的action会被加载actions
的中间。开始按照列表顺序执行。

```
def Task exeAnalysis=task exeAnalysis(type:ExeAnalysis){
//    println "exeAnalysis: do jb -----! description:${description}" //不会走
}
exeAnalysis.doFirst {
    println "exeAnalysis: doFirst-----! description:${description}"
}

exeAnalysis.doLast {
    println "exeAnalysis: doLast-----! description:${description}"
}

class ExeAnalysis extends DefaultTask{

    @TaskAction //这个必须要添加 ，表示加入到actions列表中
    def doSelf(){
        println "exeAnalysis: doSelf-----! description:${description}"
    }
}
```

## 4.6 任务的顺序执行

可以操作两个任务的先后顺序，来控制任务的执行顺序。mustRunAfter/shouldRunAfter 比如： publis任务必须要在assemble任务之后执行。
assemble任务必须要在clean任务后执行。简单的一笔啊！

比如： 打包必须要在单元测试后执行，打包成功后才能进行部署发布等。

## 4.7 任务的启用和禁用

可以通过修改属性来跳过某个任务

```
tasks.create("exe10"){
    doLast {
        println "任务的启用功能 dolast！"
    }

}
tasks.findByName("exe10").enabled(false)
tasks.findByName("exe10").enabled(true)
```

## 4.7 任务的启用和禁用












































