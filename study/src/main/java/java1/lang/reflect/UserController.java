package java1.lang.reflect;

public class UserController {

    @Autowired
    private UserService userService;

    public UserService getUserService() {
        return userService;
    }

}
