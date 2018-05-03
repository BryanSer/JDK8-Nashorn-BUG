import java.util.function.Consumer;
import javax.script.ScriptEngineManager;
import jdk.nashorn.api.scripting.NashornScriptEngine;
/**
 *
 * @author Bryan_lzh
 * @version 1.0
 */
public class ScriptLoader {

    /*  
        nashorn脚本引擎存在一个明显的BUG
        位于 jdk.nashorn.api.scripting.NashornScriptEngineFactory:431的静态方法getAppClassLoader
        其中字节码描述如下: 
             * 0: invokestatic  java/lang/Thread.currentThread:()Ljava/lang/Thread;
             * 3: invokevirtual java/lang/Thread.getContextClassLoader:()Ljava/lang/ClassLoader;
             * 6: astore_0
             * 7: aload_0
             * 8: ifnonnull     19
             * 11: ldc           jdk/nashorn/api/scripting/NashornScriptEngineFactory
             * 13: invokevirtual java/lang/Class.getClassLoader:()Ljava/lang/ClassLoader;
             * 16: goto          20
             * 19: aload_0
             * 20: areturn
        显然 代码返回的类加载器首先选取当前线程的类加载器(位于Thread.currentThread().getContextClassLoader())
        若返回null则返回NashornScriptEngineFactory的类加载器(NashornScriptEngineFactory.class.getClassLoader())
        这意味着 通过构造ScriptEngineManager所传入的类加载器没有任何用处
        导致返回的ScriptEngine不使用所指定的类加载器
        故本方法做了修改当前线程类加载器的修改
     */

    /**
     * 修复BUG的方法<p>
     * 注 因为是采用修改当前线程的classloader所以需要传入参数Consumer<NashornScriptEngine>来完成对脚本的执行
     *
     * @param c 可为null
     * @param cl
     * @return 可以直接对返回的脚本引擎进行操作 不存在classloader错误问题
     */
    public static NashornScriptEngine eval(Consumer<NashornScriptEngine> c, ClassLoader cl) {
        ClassLoader backup = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(cl);
        ScriptEngineManager EngineManager = new ScriptEngineManager(cl);
        NashornScriptEngine ns = (NashornScriptEngine) EngineManager.getEngineByName("nashorn");
        if (c != null) {
            c.accept(ns);
        }
        Thread.currentThread().setContextClassLoader(backup);
        return ns;
    }
}
