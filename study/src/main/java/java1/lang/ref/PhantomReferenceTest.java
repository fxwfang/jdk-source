package java1.lang.ref;

import org.junit.Test;

import java.lang.ref.PhantomReference;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;

public class PhantomReferenceTest {

    /**
     * 虚引用
     * “虚引用”顾名思义，就是形同虚设，与其他几种引用都不同，虚引用并不会决定对象的生命周期。如果一个对象仅持有虚引用，
     * 那么它就和没有任何引用一样，在任何时候都可能被垃圾回收器回收。
     * <p>
     * 虚引用主要用来跟踪对象被垃圾回收器回收的活动。虚引用与软引用和弱引用的一个区别在于：虚引用必须和引用队列
     * （ReferenceQueue）联合使用。当垃圾回收器准备回收一个对象时，如果发现它还有虚引用，
     * 就会在回收对象的内存之前，把这个虚引用加入到与之 关联的引用队列中。
     */
    @Test
    public void test01() throws InterruptedException {
        ReferenceQueue refQueue = new ReferenceQueue();

        //10mb
        byte[] buffer = new byte[1024 * 1024 * 10];
        PhantomReference phantomReference = new PhantomReference(buffer, refQueue);
        //字节数组失去强引用
        buffer = null;
        Reference ref0 = refQueue.poll();
        System.out.println("gc执行之前refQueue中是否有数据？ " + (ref0 != null ? "Yes" : "NO"));
        System.out.println("gc执行前，ref引用的对象：" + phantomReference.get());  // 一定会返回null

        System.gc();
        Thread.sleep(1000); // 确保gc执行程序

        System.out.println("gc执行后，ref引用的对象：" + phantomReference.get());
        // buffer被回收后，weakReference会被放入refQueue
        // 使用： WeakHashMap
        Reference ref = refQueue.poll();
        System.out.println("gc执行之后refQueue中是否有数据？ " + (ref != null ? "Yes" : "NO"));
        System.out.println("refQueue中获取的ref与weakReferences是否一致？" + (ref == phantomReference ? "yes" : "no"));

    }
}
