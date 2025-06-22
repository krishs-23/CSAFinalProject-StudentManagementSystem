/*
 * Krish Senthil
 *
 * Period 1
 * APCSA - Final Project - Student Management System - System Manager Class
 * 06/11/2025
 *
 * The SystemManager class is the central coordinator of the student management system,
 * managing students, classes, grades, and curving operations. It provides methods to add,
 * edit, remove, and manage students, classes, and grades, orchestrating all system functionality.
 */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;

public class SystemManager {
    private ArrayList<RegularStudent> students;
    private ArrayList<Class> classes;
    private ArrayList<GradeRecord> gradeRecords;
    private int totalEnrollments;

    public SystemManager() {
        this.students = new ArrayList<>();
        this.classes = new ArrayList<>();
        this.gradeRecords = new ArrayList<>();
        this.totalEnrollments = 0;
    }

    /**
     * Finds a student by their ID.
     * @param studentId The ID of the student to find.
     * @return An Optional containing the student if found, otherwise empty.
     */
    public Optional<RegularStudent> findStudentById(String studentId) {
        return students.stream()
                .filter(s -> s.getId().equals(studentId))
                .findFirst();
    }

    /**
     * Adds a student to the system.
     * @param student the student to add
     */
    public void addStudent(RegularStudent student) {
        if (student != null && findStudentById(student.getId()).isEmpty()) {
            this.students.add(student);
        }
    }

    /**
     * Removes a student from the system.
     * @param studentId The ID of the student to remove.
     * @return true if the student was found and removed, false otherwise.
     */
    public boolean removeStudent(String studentId) {
        Optional<RegularStudent> studentToRemoveOpt = findStudentById(studentId);
        if (studentToRemoveOpt.isEmpty()) {
            return false;
        }
        RegularStudent studentToRemove = studentToRemoveOpt.get();

        int enrollmentsRemoved = studentToRemove.getClasses().size();
        this.totalEnrollments -= enrollmentsRemoved;

        for (Class c : classes) {
            c.removeStudent(studentToRemove);
        }

        gradeRecords.removeIf(record -> record.getStudentID().equals(studentId));
        students.remove(studentToRemove);

        return true;
    }

    /**
     * Edits an existing student's details.
     * @param studentId The ID of the student to edit.
     * @param newName The new name for the student.
     * @param newGradeLevel The new grade level for the student.
     * @return true if the student was found and edited, false otherwise.
     */
    public boolean editStudent(String studentId, String newName, int newGradeLevel) {
        Optional<RegularStudent> studentOpt = findStudentById(studentId);
        if (studentOpt.isPresent()) {
            RegularStudent student = studentOpt.get();
            student.setName(newName);
            student.setGradeLevel(newGradeLevel);
            return true;
        }
        return false;
    }

    /**
     * Adds a class to the system.
     * @param course the class to add
     */
    public void addClass(Class course) {
        if (course != null && classes.stream().noneMatch(c -> c.getClassCode().equals(course.getClassCode()))) {
            this.classes.add(course);
        }
    }

    /**
     * Enrolls a student in a class and updates the total enrollment count.
     * @param student The student to enroll.
     * @param course The class to enroll the student in.
     */
    public void enrollStudent(RegularStudent student, Class course) {
        if (student != null && course != null) {
            if (student.getClasses().stream().noneMatch(c -> c.getClassCode().equals(course.getClassCode()))) {
                student.enrollClass(course);
                this.totalEnrollments++;
            }
        }
    }

    /**
     * Assigns a grade to a student for a class.
     * @param studentID the student's ID
     * @param classCode the class code
     * @param grade the grade to assign
     */
    public void assignGrade(String studentID, String classCode, double grade) {
        gradeRecords.add(new GradeRecord(studentID, classCode, grade));
        findStudentById(studentID).ifPresent(student -> student.calculateGPA(this.gradeRecords));
    }

    /**
     * Imports students and their class/grade data from a complex CSV file.
     * @param filePath The path to the student CSV file.
     */
    public void importStudentsFromCSV(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line = reader.readLine();

            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length < 4) continue;

                for (int i = 0; i < data.length; i++) {
                    data[i] = data[i].replace("\"", "").trim();
                }

                String id = data[0].trim();
                String name = data[1].trim();
                int gradeLevel = Integer.parseInt(data[2].trim());
                String type = data[3].trim();

                RegularStudent student;
                if (findStudentById(id).isEmpty()) {
                    if ("AP".equalsIgnoreCase(type)) {
                        student = new APStudent(id, name, gradeLevel);
                    } else {
                        student = new RegularStudent(id, name, gradeLevel);
                    }
                    this.addStudent(student);
                } else {
                    student = findStudentById(id).get();
                }

                for (int i = 4; i < data.length; i += 2) {
                    if (i + 1 < data.length) {
                        String classCode = data[i].trim();
                        double grade = Double.parseDouble(data[i + 1].trim());

                        Class course = findOrCreateClass(classCode);

                        this.enrollStudent(student, course);
                        this.assignGrade(student.getId(), classCode, grade);
                    }
                }
            }
            System.out.println("Student data imported successfully from " + filePath);
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error importing student data: " + e.getMessage());
        }
    }

    /**
     * Finds a class by its code, or creates it if it doesn't exist.
     * @param classCode The code of the class to find or create.
     * @return The existing or newly created Class object.
     */
    private Class findOrCreateClass(String classCode) {
        for (Class course : classes) {
            if (course.getClassCode().equals(classCode)) {
                return course;
            }
        }
        Class newClass = new Class(classCode, "Class: " + classCode, "Staff", 1);
        this.addClass(newClass);
        return newClass;
    }

    /**
     * Exports the current student roster to a CSV file.
     * @param filePath The path where the CSV file will be saved.
     */
    public void exportStudentsToCSV(String filePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write("StudentID,Name,GradeLevel,StudentType,GPA\n");
            for (RegularStudent student : students) {
                String type = (student instanceof APStudent) ? "AP" : "Regular";
                String line = String.join(",",
                        student.getId(),
                        student.getName(),
                        String.valueOf(student.getGradeLevel()),
                        type,
                        String.format("%.2f", student.getGPA())
                );
                writer.write(line);
                writer.newLine();
            }
            System.out.println("Students exported successfully to " + filePath);
        } catch (IOException e) {
            System.err.println("Error exporting students to CSV: " + e.getMessage());
        }
    }

    /**
     * Returns a list of all students in the system.
     * @return An ArrayList of all students.
     */
    public ArrayList<RegularStudent> getStudents() {
        return this.students;
    }

    /**
     * Retrieves all grade records for a specific student.
     * @param studentId The ID of the student.
     * @return An ArrayList of GradeRecord objects for the specified student.
     */
    public ArrayList<GradeRecord> getGradesForStudent(String studentId) {
        return gradeRecords.stream()
                .filter(record -> record.getStudentID().equals(studentId))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Generates a report of all students and grades.
     * @return string containing the report
     */
    public String generateReport() {
        StringBuilder report = new StringBuilder("\nStudent Management System Report\n");
        report.append("====================================\n");
        report.append("Total Students: ").append(this.students.size()).append("\n");
        report.append("Total Enrollments: ").append(this.totalEnrollments).append("\n\n");
        report.append("Students:\n");
        report.append("---------");
        if (students.isEmpty()){
            report.append("\nNo students in the system.");
        } else {
            for (RegularStudent student : this.students) {
                report.append("\nID: ").append(student.getId())
                        .append(", Name: ").append(student.getName())
                        .append(String.format(", GPA: %.2f", student.getGPA()));
            }
        }
        report.append("\n\nGrades:\n");
        report.append("-------");
        if (gradeRecords.isEmpty()) {
            report.append("\nNo grade records in the system.");
        } else {
            for (GradeRecord record : this.gradeRecords) {
                report.append("\nStudent ID: ").append(record.getStudentID())
                        .append(", Class: ").append(record.getClassCode())
                        .append(String.format(", Grade: %.2f", record.getGrade()))
                        .append(", Passing: ").append(record.isPassing() ? "Yes" : "No");
            }
        }
        report.append("\n====================================\n");
        return report.toString();
    }
}
