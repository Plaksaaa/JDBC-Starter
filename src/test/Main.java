package test;

import test.database.dao.PhoneDao;
import test.database.dao.UserDao;
import test.database.dto.UserFilter;
import test.database.entity.User;


public class Main {
    public static void main(String[] args) {

    }

    private static void findAllPhonesTest() {
        var instance = PhoneDao.getInstance();
        instance.findAll()
                .forEach(System.out::println);
    }

    private static void testJoin() {
        var user = UserDao.getInstance().findById(3);
        System.out.println(user);
    }

    private static void findAllFilterTest() {
        var instance = UserDao.getInstance();
        var nekit = instance.findAll(new UserFilter(3, 0, "Andrew", null));
        System.out.println(nekit);
    }

    private static void insertTest() {
        var userDao = UserDao.getInstance();
        var lol = new User();
        lol.setFirstName("Nastechka");
        lol.setLastName("Nyashka");
        lol.setUserName("Djudjuka");
        lol.setPass("Barbidokskaya");
        lol.setLocation("Dead");
        lol.setGender("African Volk");
        var save = userDao.save(lol);
        System.out.println(save);
    }

    private static void updateTest() {
        var userDao = UserDao.getInstance();
        var byId = userDao.findById(10);
        System.out.println(byId);

        byId.ifPresent(user -> {
            user.setPass("lol");
            userDao.update(user);
        });
    }

        /*WalkThread walk = new WalkThread(); // new thread object
        Thread talk = new Thread(new TalkThread()); // new thread object
        talk.start(); // start of thread
        walk.start(); // start of thread*/
    // TalkThread t = new TalkThread(); just an object, not a thread // t.run(); or talk.run();
    // method will execute, but thread will not start!
        /*List<String> str = List.of("ma mother is dangerous but she is ma mother".split(" "));
        str.forEach(System.out::println);
        str.stream()
                .filter(s -> s.length() < 3)

               .forEach(s -> System.out.print(s + " "));*/
}
