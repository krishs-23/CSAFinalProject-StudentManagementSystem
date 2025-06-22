/*
 * Krish Senthil
 *
 * Period 1
 * APCSA - Final Project - Student Management System - Main Class
 * 06/11/2025
 *
 * The Main class provides a unified, interactive command-line interface (CLI) to manage the system.
 * It allows users to manage students, import/export data, view reports, and use a standalone
 * tool to curve grades for a single assignment from a CSV file.
 */

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Optional;
import java.util.Scanner;

public class Main {
    private static SystemManager manager = new SystemManager();
    private static Scanner scanner = new Scanner(System.in);

    // The main menu for the application
    public static void main(String[] args) {
        System.out.println("Welcome to the Student Management System!");

        while (true) {
            printMainMenu();
            int choice = getUserChoice();

            if (choice == 1) {
                handleStudentManagement();
            } else if (choice == 2) {
                importStudentsCSV();
            } else if (choice == 3) {
                exportStudentsCSV();
            } else if (choice == 4) {
                handleAssignmentCurving();
            } else if (choice == 5) {
                System.out.println(manager.generateReport());
            } else if (choice == 6) {
                System.out.println("Exiting system. Goodbye!");
                scanner.close();
                return;
            } else {
                System.out.println("Invalid choice. Please enter a number between 1 and 6.");
            }

            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
        }
    }

    // Prints the main menu options to the console
    private static void printMainMenu() {
        System.out.println("\n--- Main Menu ---");
        System.out.println("1. Manage Students (View, Add, Edit, Remove)");
        System.out.println("2. Import Students & Grades from CSV");
        System.out.println("3. Export Student Roster to CSV");
        System.out.println("4. Curve Assignment Grades");
        System.out.println("5. Generate Full System Report");
        System.out.println("6. Exit");
        System.out.print("Enter your choice: ");
    }

    // Displays the student management sub-menu and handles user input for it
    private static void handleStudentManagement() {
        while (true) {
            System.out.println("\n--- Student Management ---");
            System.out.println("1. View Student Details & Grades");
            System.out.println("2. Add New Student Manually");
            System.out.println("3. Edit Existing Student");
            System.out.println("4. Remove Student");
            System.out.println("5. Back to Main Menu");
            System.out.print("Enter your choice: ");
            int choice = getUserChoice();

            if (choice == 1) {
                viewStudentDetails();
            } else if (choice == 2) {
                addStudentManually();
            } else if (choice == 3) {
                editStudent();
            } else if (choice == 4) {
                removeStudent();
            } else if (choice == 5) {
                return;
            } else {
                System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    // Handles the logic for viewing a specific student's details and grades.
    private static void viewStudentDetails() {
        ArrayList<RegularStudent> students = manager.getStudents();
        if (students.isEmpty()) {
            System.out.println("There are no students in the system.");
            return;
        }

        System.out.println("\n--- Student Roster ---");
        for (RegularStudent s : students) {
            System.out.println("ID: " + s.getId() + ", Name: " + s.getName());
        }

        System.out.print("\nEnter the ID of the student to view their grades: ");
        String studentId = scanner.nextLine();

        Optional<RegularStudent> studentOpt = manager.findStudentById(studentId);
        if (studentOpt.isEmpty()) {
            System.out.println("Error: No student found with that ID.");
            return;
        }

        RegularStudent student = studentOpt.get();
        ArrayList<GradeRecord> grades = manager.getGradesForStudent(studentId);

        System.out.println("\n--- Grade Report for " + student.getName() + " (ID: " + student.getId() + ") ---");
        System.out.printf("Current GPA: %.2f%n", student.getGPA());
        System.out.println("--------------------------------------------------");

        if (grades.isEmpty()) {
            System.out.println("This student has no grade records.");
        } else {
            for (GradeRecord grade : grades) {
                System.out.printf("Class Code: %-10s | Grade: %-6.2f | Passing: %s%n",
                        grade.getClassCode(), grade.getGrade(), (grade.isPassing() ? "Yes" : "No"));
            }
        }
        System.out.println("--------------------------------------------------");
    }

    // Handles the logic for adding a new student to the system via console input
    private static void addStudentManually() {
        System.out.print("Enter Student ID: ");
        String id = scanner.nextLine();
        if (manager.findStudentById(id).isPresent()) {
            System.out.println("Error: A student with this ID already exists.");
            return;
        }
        System.out.print("Enter Student Name: ");
        String name = scanner.nextLine();
        System.out.print("Enter Grade Level (e.g., 9, 10, 11): ");
        int gradeLevel = getIntInput();
        System.out.print("Enter Student Type (AP or Regular): ");
        String type = scanner.nextLine();

        if ("AP".equalsIgnoreCase(type)) {
            manager.addStudent(new APStudent(id, name, gradeLevel));
        } else {
            manager.addStudent(new RegularStudent(id, name, gradeLevel));
        }
        System.out.println("Student added successfully!");
    }

    // Handles the logic for editing an existing student's details
    private static void editStudent() {
        System.out.print("Enter the ID of the student to edit: ");
        String id = scanner.nextLine();
        if (manager.findStudentById(id).isEmpty()) {
            System.out.println("Error: No student found with that ID.");
            return;
        }

        System.out.print("Enter new name: ");
        String newName = scanner.nextLine();
        System.out.print("Enter new grade level: ");
        int newGradeLevel = getIntInput();

        if (manager.editStudent(id, newName, newGradeLevel)) {
            System.out.println("Student updated successfully.");
        } else {
            System.out.println("Failed to update student.");
        }
    }

    // Handles the logic for removing a student from the system
    private static void removeStudent() {
        System.out.print("Enter the ID of the student to remove: ");
        String id = scanner.nextLine();
        if (manager.removeStudent(id)) {
            System.out.println("Student successfully removed from the system.");
        } else {
            System.out.println("Error: No student found with that ID.");
        }
    }

    // Handles the logic for importing students from a CSV file
    private static void importStudentsCSV() {
        System.out.print("Enter the path to the CSV file to import student data from: ");
        String path = scanner.nextLine();
        manager.importStudentsFromCSV(path);
    }

    // Handles the logic for exporting students to a CSV file
    private static void exportStudentsCSV() {
        System.out.print("Enter the file path to export the student roster to (e.g., export.csv): ");
        String path = scanner.nextLine();
        manager.exportStudentsToCSV(path);
    }

    // Handles logic for the assignment curving tool
    private static void handleAssignmentCurving() {
        AutoCurver curver = new AutoCurver();

        System.out.println("\n--- Assignment Grade Curver ---");
        System.out.print("Enter the path to the input CSV with scores (Format: StudentID,Score): ");
        String inputFile = scanner.nextLine();

        System.out.print("Enter the path for the new output file (e.g., curved_scores.csv): ");
        String outputFile = scanner.nextLine();

        printCurveDescriptions();

        System.out.print("Enter the curve type from the list above: ");
        String curveType = scanner.nextLine().trim().toLowerCase();

        double curveValue = 0.0;
        if (curveType.equals("flat") || curveType.equals("stddev") || curveType.equals("power")) {
            System.out.print("Enter the required numeric value for this curve: ");
            try {
                curveValue = scanner.nextDouble();
                scanner.nextLine();
            } catch (InputMismatchException e) {
                System.out.println("Invalid number. Using 0.0 for curve value.");
                scanner.nextLine();
            }
        }

        System.out.println("\nReading scores...");
        ArrayList<AssignmentScore> scores = curver.readScores(inputFile);

        if (!scores.isEmpty()) {
            System.out.println("Applying " + curveType + " curve...");
            curver.applyCurve(scores, curveType, curveValue);

            System.out.println("Saving curved scores...");
            curver.saveCurvedScores(scores, outputFile);

            System.out.println("\nProcess complete! Curved scores have been saved to: " + outputFile);
        } else {
            System.out.println("\nCould not process scores. Please check the input file and try again.");
        }
    }

    // Prints a detailed description of all available curve types
    private static void printCurveDescriptions() {
        System.out.println("\n--- Available Curve Types ---");
        System.out.println("  flat    : Adds a fixed number of points to every score.");
        System.out.println("            -> Value: The number of points to add (e.g., 5).");
        System.out.println("  sqrt    : Takes the square root of the score and multiplies by 10.");
        System.out.println("            -> No value needed.");
        System.out.println("  log     : Applies a logarithmic curve to compress higher scores.");
        System.out.println("            -> No value needed.");
        System.out.println("  exp     : Applies an exponential curve to boost lower scores more significantly.");
        System.out.println("            -> No value needed.");
        System.out.println("  power   : Raises the normalized score (0-1) to a given power.");
        System.out.println("            -> Value: The exponent to use (e.g., 0.5 to boost scores).");
        System.out.println("  sigmoid : A smooth 'S'-shaped curve that boosts scores around the midpoint (50).");
        System.out.println("            -> No value needed.");
        System.out.println("  z-score : Standardizes scores and rescales them to a target mean and standard deviation.");
        System.out.println("            -> No value needed (uses default mean 75, stddev 10).");
        System.out.println("  stddev  : Shifts all scores to achieve a new target mean score.");
        System.out.println("            -> Value: The desired average score for the class (e.g., 85).");
        System.out.println("  ratio   : Assigns letter grades based on a fixed percentage distribution (e.g., top 10% get an A).");
        System.out.println("            -> No value needed.");
        System.out.println("-----------------------------\n");
    }

    /**
     * Gets the user's integer menu choice.
     * @return The user's choice, or -1 if the input is invalid.
     */
    private static int getUserChoice() {
        try {
            int choice = scanner.nextInt();
            scanner.nextLine();
            return choice;
        } catch (InputMismatchException e) {
            scanner.nextLine();
            return -1;
        }
    }

    /**
     * Gets an integer input from the user, retrying until valid input is given.
     * @return The valid integer input from the user.
     */
    private static int getIntInput() {
        while (true) {
            try {
                int input = scanner.nextInt();
                scanner.nextLine();
                return input;
            } catch (InputMismatchException e) {
                System.out.print("Invalid input. Please enter a whole number: ");
                scanner.nextLine();
            }
        }
    }
}
