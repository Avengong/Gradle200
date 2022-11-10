package com.ztsdk.lib.plugin

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.FieldVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes


class HelloClassVisitor(var cv: ClassVisitor) : ClassVisitor(Opcodes.ASM7, cv) {

    // 这个方法就是classReader访问到class的方法时调用过来的
    override fun visitMethod(access: Int, name: String?, descriptor: String?, signature: String?, exceptions: Array<out String>?): MethodVisitor {
        // 拿到methodVisitor对象，它会被classReader调用来反问方法的属性：如注解、参数列表等。因此，我们需要在封装一下这个方法的访问
        println("开始访问【方法了  name:$name , descriptor:$descriptor, signature:$signature")

        val methodVisitor = cv.visitMethod(access, name, descriptor, signature, exceptions)
        return HelloMethodVisitor(api, methodVisitor, access, name, descriptor, signature)
    }

    override fun visitField(access: Int, name: String?, descriptor: String?, signature: String?, value: Any?): FieldVisitor {

        return super.visitField(access, name, descriptor, signature, value)
    }
}