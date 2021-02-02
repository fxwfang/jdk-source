package java1.lang.ref;

import org.junit.Test;

import java.lang.ref.Reference;

public class ReferenceTest {
    /**
     * Reference: 特殊的类，垃圾回收的时候可以被JVM识别，可达性分析的时候，reference指向的不算
     * <p>
     * Reference的4种状态： Active,Pending,Enqueued,Inactive
     * <p>
     * --> active --> gc ----->Pending -->是否指定了refQueue
     *                                // yes --> Enqueued -- > Inactive
     *                                // no  --> Inactive
     */
    @Test
    public void ReferenceTest() {
        Reference reference =null;
    }
}
