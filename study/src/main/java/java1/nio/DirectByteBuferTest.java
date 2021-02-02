package java1.nio;

import org.junit.Test;
import sun.misc.Cleaner;

import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;

public class DirectByteBuferTest {

    @Test
    public void main(){
        ByteBuffer direct = ByteBuffer.allocateDirect(8);//创建8字节空间的直接内存
       // ByteBuffer和普通java对象一样，是通过gc回收的，但gc并不管理直接内存，ByteBuffer指向的直接内存空间是如何被释放的呢？
        System.gc();
        direct.clear();
//        direct.
        System.out.println(direct);
        ReferenceQueue queue = new ReferenceQueue();
        PhantomReference ref1 = new PhantomReference(new Object(), queue);
        System.gc();
        System.out.println("----->>>");
        WeakReference ref = new WeakReference(new Object());
        System.gc();
        //
//        Cleaner c =null;
//        c.clean();
    }
}
