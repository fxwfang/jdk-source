package java1.lang.ref;

import org.junit.Test;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;

public class SoftReferenceTest {

    /**
     * 软引用
     * 如果一个对象只具有软引用，则内存空间足够，垃圾回收器就不会回收它；如果内存空间不足了，就会回收这些对象的内存。
     * 只要垃圾回收器没有回收它，该对象就可以被程序使用。软引用可用来实现内存敏感的高速缓存。
     * <p>
     * 软引用可以和一个引用队列（ReferenceQueue）联合使用，如果软引用所引用的对象被垃圾回收器回收，
     * Java虚拟机就会把这个软引用加入到与之关联的引用队列中。
     *
     * 输出结果：
     * null
     * null
     * null
     * null
     * null
     * null
     * null
     * null
     * null
     * [B@13a5fe33
     * [B@3108bc
     * [B@370736d9
     * [B@5f9d02cb
     */
    @Test
    public void SoftReferenceTest() throws InterruptedException {
        List<Reference> refList = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            byte[] buffer = new byte[1024 * 1024 * 10];
            SoftReference softReference = new SoftReference(buffer);
            refList.add(softReference);
        }

        System.gc();
        Thread.sleep(1000);

        for(Reference ref :  refList){
            System.out.println(ref.get());
        }

    }


}
