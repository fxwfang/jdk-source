package java1.lang.ref;

import org.junit.Test;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;

public class WeakReferenceTest {


    //弱引用

    /**
     * 弱引用与软引用的区别在于：只具有弱引用的对象拥有更短暂的生命周期。在垃圾回收器线程扫描它所管辖的内存区域的过程中，
     * 一旦发现了只具有弱引用的对象，不管当前内存空间足够与否，都会回收它的内存。不过，由于垃圾回收器是一个优先级很低的线程，
     * 因此不一定会很快发现那些只具有弱引用的对象。
     * <p>
     * 弱引用可以和一个引用队列（ReferenceQueue）联合使用，如果弱引用所引用的对象被垃圾回收，Java虚拟机就会把这个弱引用加
     * 入到与之关联的引用队列中。
     *
     * @throws InterruptedException
     */
    @Test
    public void test01() throws InterruptedException {
        ReferenceQueue refQueue = new ReferenceQueue();

        //10mb
        byte[] buffer = new byte[1024 * 1024 * 10];
        WeakReference weakReference = new WeakReference(buffer, refQueue);
        //字节数组失去强引用
        buffer = null;
        Reference ref0 = refQueue.poll();
        System.out.println("gc执行之前refQueue中是否有数据？ " + (ref0 != null ? "Yes" : "NO"));
        System.out.println("gc执行前，ref引用的对象：" + weakReference.get());

        System.gc();
        Thread.sleep(1000); // 确保gc执行程序

        System.out.println("gc执行后，ref引用的对象：" + weakReference.get());
        // buffer被回收后，weakReference会被放入refQueue
        // 使用： WeakHashMap
        Reference ref = refQueue.poll();
        System.out.println("gc执行之后refQueue中是否有数据？ " + (ref != null ? "Yes" : "NO"));
        System.out.println("refQueue中获取的ref与weakReferences是否一致？" + (ref == weakReference ? "yes" : "no"));

    }
}
