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
        mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        mv.visitLdcInsn("Hello World!");
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false)
        if (name.equals("onCreate")) {
            mv.visitMethodInsn(INVOKESTATIC, "com/ztsdk/lib/gradletwo/ThreadUtils", "test", "()V", false)

            mv.visitLdcInsn("MainActivity");
            mv.visitMethodInsn(INVOKESTATIC, "com/ztsdk/lib/gradletwo/ThreadUtils", "printName", "(Ljava/lang/String;)V", false);

        }

    }

    override fun onMethodExit(opcode: Int) {
        super.onMethodExit(opcode)

    }


}