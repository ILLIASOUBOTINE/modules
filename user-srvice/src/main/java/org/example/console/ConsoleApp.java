package org.example.console;

import org.example.service.UserService;
import org.example.dto.UserUpdateDto;
import org.example.entity.User;

import java.util.List;
import java.util.Scanner;

/**
 * Console-based application for managing {@link User} entities.
 * <p>
 * Provides a simple interactive menu for performing CRUD operations on users:
 * create, read (by id or all), update, and delete.
 * <p>
 * Uses {@link UserService} to perform business operations and handle database interactions.
 */
public class ConsoleApp {

    /**
     * Service layer used for all user operations.
     */
    private final UserService userService;

    /**
     * Scanner for reading user input from the console.
     */
    private final Scanner scanner = new Scanner(System.in);

    /**
     * Constructs the console application with the specified {@link UserService}.
     *
     * @param userService service instance for performing user operations
     */
    public ConsoleApp(UserService userService) {
        this.userService = userService;
    }

    /**
     * Starts the console application and shows the menu in a loop until exit.
     */
    public void start() {
        while (true) {
            printMenu();
            try {
                int choice = Integer.parseInt(scanner.nextLine());

                switch (choice) {
                    case 1 -> createUser();
                    case 2 -> getUser();
                    case 3 -> getAllUsers();
                    case 4 -> updateUser();
                    case 5 -> deleteUser();
                    case 0 -> {
                        System.out.println("Exit...");
                        return;
                    }
                    default -> System.out.println("Wrong choice");
                }

            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Please enter a number.");
            } catch (Exception e) {
                System.out.println("Unexpected error: " + e.getMessage());
            }
        }
    }

    /**
     * Prints the main menu to the console.
     */
    private void printMenu() {
        System.out.println("""
                     Menu:
                1. Create user
                2. Get user by id
                3. Get all users
                4. Update user
                5. Delete user
                0. Exit
                """);
    }

    /**
     * Reads input from the console and creates a new user.
     * <p>
     * Handles exceptions and displays error messages in case of invalid input or database errors.
     */
    private void createUser() {
        try {
            System.out.print("Name: ");
            String name = scanner.nextLine();

            System.out.print("Email: ");
            String email = scanner.nextLine();

            System.out.print("Age: ");
            int age = Integer.parseInt(scanner.nextLine());

            userService.createUser(name, email, age);
            System.out.println("User created!");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * Reads a user ID from the console and prints the corresponding user.
     */
    private void getUser() {
        try {
            System.out.print("Enter id: ");
            Long id = Long.parseLong(scanner.nextLine());

            User user = userService.getUser(id);
            System.out.println(user);

        } catch (NumberFormatException e) {
            System.out.println("Invalid ID format!");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * Retrieves and prints all users from the database.
     * Prints a message if no users exist.
     */
    private void getAllUsers() {
        try {
            List<User> users = userService.getAllUsers();
            if (users.isEmpty()) {
                System.out.println("No users in the database!");
            } else {
                System.out.println("All users:");
                users.forEach(System.out::println);
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * Reads user input to update an existing user.
     * <p>
     * Empty inputs are interpreted as "no change" for the corresponding field.
     * Exceptions are caught and printed to the console.
     */
    private void updateUser() {
        try {
            System.out.print("Enter id: ");
            Long id = Long.parseLong(scanner.nextLine());

            System.out.print("New name (or empty): ");
            String name = scanner.nextLine();

            System.out.print("New email (or empty): ");
            String email = scanner.nextLine();

            System.out.print("New age (or empty): ");
            String ageStr = scanner.nextLine();

            UserUpdateDto dto = new UserUpdateDto(
                    name.isEmpty() ? null : name,
                    email.isEmpty() ? null : email,
                    ageStr.isEmpty() ? null : Integer.parseInt(ageStr)
            );

            userService.updateUser(id, dto);
            System.out.println("Updated!");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * Reads a user ID from the console and deletes the corresponding user.
     */
    private void deleteUser() {
        try {
            System.out.print("Enter id: ");
            Long id = Long.parseLong(scanner.nextLine());

            userService.deleteUser(id);
            System.out.println("Deleted!");

        } catch (NumberFormatException e) {
            System.out.println("Invalid ID format!");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}