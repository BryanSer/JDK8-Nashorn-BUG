##JDK Version:  _1.8.0_131_
##BUG Description: When construct[ScriptEngineManager](https://docs.oracle.com/javase/8/docs/api/javax/script/ScriptEngineManager.html)to get Nashorn engine and the ClassLoader which you send to constructor isn't use to construct NashornScriptEngine.
##中文文描述请见[此处](https://coding.net/u/Bryan_lzh/p/JDK8-Nashorn-BUG/git)
---
##Reason: 
###At _jdk.nashorn.api.scripting.NashornScriptEngineFactory:431_ static method *getAppClassLoader()*
###byte code: 
>              * 0: invokestatic  java/lang/Thread.currentThread:()Ljava/lang/Thread;
             * 3: invokevirtual java/lang/Thread.getContextClassLoader:()Ljava/lang/ClassLoader;
             * 6: astore_0
             * 7: aload_0
             * 8: ifnonnull     19
             * 11: ldc           jdk/nashorn/api/scripting/NashornScriptEngineFactory
             * 13: invokevirtual java/lang/Class.getClassLoader:()Ljava/lang/ClassLoader;
             * 16: goto          20
             * 19: aload_0
             * 20: areturn

###        Obviously the ClassLoader that returned is current thread's ClassLoader[^1]
###        if what it returned is null then return NashornScriptEngineFactory's ClassLoader[^2]
###        this means to construct[ScriptEngineManager(ClassLoader loader)](https://docs.oracle.com/javase/8/docs/api/javax/script/ScriptEngineManager.html#ScriptEngineManager-java.lang.ClassLoader-)with a ClassLoader is useless.
---
##Repair: 
###because getAppClassLoader returned classloader is current thread's ClassLoader
###so before ScriptEngineManager get Nashorn, edit the current thread's ClassLoader to achive the NashornScriptEngine use you gave classloader

[^1]: at Thread.currentThread().getContextClassLoader()
[^2]: NashornScriptEngineFactory.class.getClassLoader()
