package com.ztsdk.lib.plugin

import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.commons.AdviceAdapter


class HelloMethodVisitor(api: Int, methodVisitor: MethodVisitor, access: Int, name: String?, var descriptor: String?, signature: String?) :
    AdviceAdapter(api, methodVisitor, access, name, descriptor) {

    override fun onMethodEnter() {
        super.onMethodEnter()
//        System.out.println("Hello World!")
        println("onMethodEnter-----name:$name,descriptor:$descriptor")
        //opcode, owner, name,descriptor
//        mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
//        mv.visitLdcInsn("Hello World!");
//        mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false)


//        if (name.equals("onCreate")) {
//            mv.visitMethodInsn(INVOKESTATIC, "com/ztsdk/lib/gradletwo/ThreadUtils", "test", "()V", false)
//
//            mv.visitLdcInsn("MainActivity");
//            mv.visitMethodInsn(INVOKESTATIC, "com/ztsdk/lib/gradletwo/ThreadUtils", "printName", "(Ljava/lang/String;)V", false);
//
//            mv.visitVarInsn(ALOAD, 0);
//            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "getClass", "()Ljava/lang/Class;", false);
//            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "getName", "()Ljava/lang/String;", false);
//            mv.visitMethodInsn(INVOKESTATIC, "com/ztsdk/lib/gradletwo/ThreadUtils", "printName", "(Ljava/lang/String;)V", false);
//
//
//
//
//            mv.visitMethodInsn(INVOKESTATIC, "com/zygote/lib/insight/api/Insight", "getInstance", "()Lcom/zygote/lib/insight/api/Insight;", false);
//            mv.visitMethodInsn(
//                INVOKESTATIC,
//                "com/zygote/lib/insight/api/Insight",
//                "newConfig",
//                "()Lcom/zygote/lib/insight/global/InsightConfig;",
//                false
//            );
//            mv.visitVarInsn(ALOAD, 0);
//            mv.visitMethodInsn(INVOKEVIRTUAL, "com/ztsdk/lib/gradletwo/MainActivity", "getApplication", "()Landroid/app/Application;", false);
//            mv.visitMethodInsn(
//                INVOKEVIRTUAL,
//                "com/zygote/lib/insight/global/InsightConfig",
//                "setApplication",
//                "(Landroid/app/Application;)Lcom/zygote/lib/insight/global/InsightConfig;",
//                false
//            );
//            mv.visitMethodInsn(
//                INVOKEVIRTUAL,
//                "com/zygote/lib/insight/api/Insight",
//                "init",
//                "(Lcom/zygote/lib/insight/global/InsightConfig;)V",
//                false
//            );
//
//        }

        if (name.equals("init")) {
            println("onMethodEnter----init method-name:$name,descriptor:$descriptor")
        }
    }

    override fun onMethodExit(opcode: Int) {
        super.onMethodExit(opcode)

    }


}