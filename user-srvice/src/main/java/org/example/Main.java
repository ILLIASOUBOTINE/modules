package org.example;


import org.example.config.HibernateConfig;
import org.example.console.ConsoleApp;
import org.example.dao.UserDao;
import org.example.dao.UserDaoImpl;
import org.example.service.UserService;
import org.example.service.UserServiceImpl;

public class Main {
    public static void main(String[] args) {

        UserDao userDao = new UserDaoImpl();
        UserService userService = new UserServiceImpl(userDao);
        ConsoleApp app = new ConsoleApp(userService);


        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Close Hibernate");
            HibernateConfig.getSessionFactory().close();
        }));

        app.start();
    }
}