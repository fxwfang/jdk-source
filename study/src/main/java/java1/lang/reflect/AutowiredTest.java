package java1.lang.reflect;

import org.junit.Test;

import java.util.stream.IntStream;
import java.util.stream.Stream;

public class AutowiredTest {
    @Test
    public void testAutowired() {
        UserController userController = new UserController();
        System.out.println("注解前 --->> " + userController.getUserService());
        Class<? extends UserController> userControllerClass = userController.getClass();
        // 遍历所有属性
        Stream.of(userControllerClass.getDeclaredFields()).forEach(field -> {
            Autowired annotation = field.getAnnotation(Autowired.class);
            if (annotation != null) {
                field.setAccessible(true);
                Class<?> type = field.getType();
                //实例化对象
                Object o = null;
                try {
                    o = type.newInstance();
                    field.set(userController, o);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });

        System.out.println("注解后的--->"+userController.getUserService());

    }
}
