# Student Management System

## Project Description
The Student Management System is a Java application designed for a high school context, simulating a simplified version of Skyward to manage student records, class enrollments, grades, and grade curving. It demonstrates AP Computer Science A concepts like object-oriented programming (inheritance, interfaces, encapsulation) and file I/O, with a command-line interface for adding students, assigning grades, and applying various grade curves from CSV files.

## File Structure
- **SchoolEntity.java**: Abstract base class for entities, defining common attributes like ID and name.
- **Grade.java**: Interface for grade-related operations, ensuring consistent grade handling.
- **RegularStudent.java**: Represents a standard high school student, managing enrollment and GPA.
- **APStudent.java**: Extends RegularStudent for AP students, handling weighted GPAs and AP points.
- **Class.java**: Represents a high school course, managing rosters and course details.
- **GradeRecord.java**: Stores a student’s grade for a class, implementing the Grade interface.
- **AutoCurver.java**: Processes grade curving from CSV files, supporting nine curve types.
- **SystemManager.java**: Coordinates the system, managing students, classes, and grade operations.
- **Main.java**: Demonstrates the system’s functionality via a command-line interface.
- **grades.csv**: Sample CSV file for testing grade curving (created by Main.java).

## Setup Instructions
1. **Install Java**: Ensure Java Development Kit (JDK) 8 or later is installed. Verify by running `java -version` in a terminal.
2. **Prepare Project Directory**:
   - Create a folder (e.g., `StudentManagementSystem`).
   - Save all `.java` files listed above in this folder.
3. **Compile the Code**:
   - Open a terminal in the project directory.
   - Run: `javac *.java`
4. **Create Sample CSV (Optional)**:
   - The `Main.java` program creates a `grades.csv` file automatically.
   - Alternatively, create `grades.csv` manually with the format below and place it in the project directory.

## Usage Guide
1. **Run the Program**:
   - In the terminal, run: `java Main`
   - This executes `Main.java`, which:
     - Creates two students (one regular, one AP).
     - Enrolls them in a sample class (Geometry).
     - Assigns grades (85, 90).
     - Creates a `grades.csv` file.
     - Applies a square root curve to the grades.
     - Prints a report and system status.
2. **Modify Grade Curving**:
   - Open `Main.java` and edit the `curveGrades` call in the `main` method.
   - Change the `curveType` to one of: `sqrt`, `log`, `exp`, `power`, `sigmoid`, `stddev`, `zscore`, `ratio`, `flat`.
   - For `stddev`, set `curveValue` to the target mean (e.g., 75.0).
   - For `flat`, set `curveValue` to the percentage points to add (e.g., 5.0).
   - Recompile and run: `javac Main.java` then `java Main`.
3. **View Output**:
   - The program prints a report showing student details, GPAs, grades, and passing status.
   - The system status shows counts of students, classes, and enrollments.
   - Check `grades.csv` for updated grades after curving.

## Sample CSV Format
The `grades.csv` file must follow this format:
```
studentID,classCode,grade
S001,MATH101,85.0
S002,MATH101,90.0
```
- **studentID**: Unique student ID (e.g., `S001`).
- **classCode**: Course code (e.g., `MATH101`).
- **grade**: Numeric grade (0.0–100.0).
- Each line represents one grade record.
- Ensure no empty fields or invalid numbers to avoid errors.

## Grade Curving Options
The `AutoCurver` class supports nine curve types, applied to grades in `grades.csv`:
1. **Square Root Curve (`sqrt`)**: Scales grades as `sqrt(grade) * 10`, boosting lower grades.
2. **Log Curve (`log`)**: Applies `25 * ln(grade + 1)`, compressing high grades.
3. **Exponential Curve (`exp`)**: Uses `(e^(grade/25) - 1) * 20`, favoring low grades.
4. **Power Curve (`power`)**: Applies `(grade/100)^2 * 100`, emphasizing lower grades.
5. **Sigmoid Curve (`sigmoid`)**: Uses `100 / (1 + e^(-0.1 * (grade - 50)))`, centering grades around 50.
6. **Standard Deviation Curve (`stddev`)**: Shifts grades to a target mean (set by `curveValue`) with the same standard deviation.
7. **Z-Score Curve (`zscore`)**: Normalizes grades to a mean of 75 and standard deviation of 10.
8. **Ratio Curve (`ratio`)**: Assigns grades based on rank (10% A=90, 20% B=80, 40% C=70, 20% D=60, 10% F=50).
9. **Flat X% Curve (`flat`)**: Adds `curveValue` percentage points to all grades, capped at 100.

## Notes
- The system is designed for a high school context, focusing on simplicity and APCSA requirements.
- Grades are capped at 0–100 after curving.
- The `Main.java` demo is hardcoded for simplicity; modify it to test other curves or add more students/classes.
- Ensure `grades.csv` is in the project directory before running, or let `Main.java` create it.
- For errors (e.g., “Invalid CSV format”), check `grades.csv` for correct format or valid numbers.
