# Student Management System

## Project Description

The Student Management System is an interactive Java command-line application developed as an AP Computer Science A final project. It models a simplified high-school records system: users can add, view, edit, and remove students; import course enrollments and grades; calculate regular and AP-student GPAs; export a roster; and generate an academic report.

A separate assignment-curving workflow reads student scores from a CSV file, applies one of nine transformations, and exports both the original and adjusted scores. This utility operates independently of the student roster: curving an assignment does not change the grade records used for GPA calculations.

The project demonstrates inheritance, interfaces, encapsulation, collections, and file I/O. It uses Java's standard library and an in-memory data model, with CSV files for importing data and exporting results. It is an educational simulation rather than a production student information system.

## File Structure

All application source files are in [`src/`](src/).

| File | Responsibility |
| --- | --- |
| [`SchoolEntity.java`](src/SchoolEntity.java) | Abstract base class for common entity information, including ID and name. |
| [`RegularStudent.java`](src/RegularStudent.java) | Stores grade level, enrolled courses, and GPA; calculates an unweighted GPA from the student's grade records. |
| [`APStudent.java`](src/APStudent.java) | Extends `RegularStudent` and overrides GPA calculation to use a 5.0 scale. Also exposes AP-point methods, which are separate from the GPA formula. |
| [`Class.java`](src/Class.java) | Represents a course, including its code, name, teacher, period, and student roster. |
| [`Grade.java`](src/Grade.java) | Interface for assigning and retrieving grades and checking passing status. |
| [`GradeRecord.java`](src/GradeRecord.java) | Implements `Grade` for a student/course pair; treats a score of 70 or higher as passing. |
| [`AssignmentScore.java`](src/AssignmentScore.java) | Stores an assignment's student ID, original score, and curved score for the standalone curving tool. |
| [`AutoCurver.java`](src/AutoCurver.java) | Reads assignment scores, applies nine curve types, and writes original/adjusted scores to CSV. |
| [`SystemManager.java`](src/SystemManager.java) | Coordinates students, courses, enrollments, grade records, GPA recalculation, CSV operations, and reports. |
| [`Main.java`](src/Main.java) | Provides interactive menus, prompts, and input handling for the application. |

The repository also contains IntelliJ project metadata. An IDE is optional: the application can be compiled and launched from a terminal. The example CSV files below are files you create yourself; the current program does not generate sample input data automatically.

## Setup Instructions

1. **Install a JDK.** Use JDK 11 or later, with both `java` and `javac` available on your PATH. The code uses `Optional.isEmpty()`, so the previous Java 8 setup instructions do not apply.

   ```bash
   java -version
   javac -version
   ```

2. **Clone the repository and enter its root directory.**

   ```bash
   git clone https://github.com/krishs-23/CSAFinalProject-StudentManagementSystem.git
   cd CSAFinalProject-StudentManagementSystem
   ```

3. **Compile the source files into a separate output directory.**

   ```bash
   mkdir -p out
   javac -d out src/*.java
   ```

   These commands use a macOS/Linux shell. In Windows Command Prompt, create the directory with `mkdir out` and use the same `javac` command.

4. **Create sample CSV files if you want to try imports or grade curving.** Copy the examples in **Sample CSV Format** into `students.csv` and `scores.csv` in the repository root.

5. **Start the application from the repository root.**

   ```bash
   java -cp out Main
   ```

No external Java libraries, database server, or API credentials are required. File paths entered in the application are resolved relative to the directory from which you launched Java, unless you enter an absolute path. Recompile after changing Java source files.

## Usage Guide

### 1. Navigate the Main Menu

The application starts with an empty in-memory roster and displays these options:

| Option | Action |
| --- | --- |
| `1` | Manage students: view details and grades, add a student, edit a student, or remove a student. |
| `2` | Import students, course enrollments, and grades from a CSV file. |
| `3` | Export the current student roster and calculated GPAs to a CSV file. |
| `4` | Run the standalone assignment grade-curving tool. |
| `5` | Generate a full system report in the terminal. |
| `6` | Exit the application. |

After a completed main-menu action, press Enter when prompted to return to the main menu.

### 2. Manage Students and Academic Records

Select **1. Manage Students** to open the student submenu.

- **View:** select a student by ID to see their GPA and course grades, including passing status.
- **Add:** enter an ID, name, grade level, and student type. Enter `AP` for an AP student; other type input is treated as Regular. Duplicate IDs are rejected by the manual-add workflow.
- **Edit:** update a student's name and grade level by ID. The submenu does not change the student ID or type.
- **Remove:** delete a student and their grade records and remove them from course rosters.
- **Back:** return to the main menu.

Adding a student manually does not assign courses or grades. The current CLI obtains those through **2. Import Students & Grades from CSV**; it does not include a separate interactive course/grade editor.

During import, missing courses are created with the supplied course code, a name of `Class: <code>`, teacher `Staff`, and period `1`. Existing student IDs are reused, and repeated enrollments in the same course are prevented. Grade assignments trigger GPA recalculation.

The GPA model is deliberately simple:

- **Regular student:** average numeric grade × 4 / 100.
- **AP student:** average numeric grade × 5 / 100.
- **No grade records:** GPA is 0.

For example, grades of 85 and 95 average to 90, producing 3.60 for a Regular student or 4.50 for an AP student. The AP formula applies to all of that student's grade records; it does not distinguish AP from regular courses or weight courses by credits. AP points stored by `addAPPoints()` are not included in this calculation.

### 3. Import, Export, and Review Reports

A short walkthrough using the sample files below:

1. Select main-menu option **2** and enter `students.csv`.
2. Press Enter when prompted, then select **5** to view the report.
3. The sample represents two students and four student-course enrollments. It produces GPAs of 3.60 for Alex and 4.00 for Jordan.
4. Select **1**, then **1** again, to inspect an individual student's grades.
5. Return to the main menu and select **3**; enter `roster_export.csv` as the output path.

The full report includes student/enrollment counts, student GPAs, and individual grade records with passing status.

**Roster export is a summary, not a complete backup.** It omits course enrollments and grade history, so it cannot restore the full session through the student-import workflow. Keep your original input data if you need to recreate a session.

### 4. Curve Assignment Grades

Select **4. Curve Assignment Grades** and enter:

1. The input score-file path, such as `scores.csv`.
2. A distinct output path, such as `curved_scores.csv`.
3. A curve keyword from the table below.
4. A numeric parameter when requested for `flat`, `power`, or `stddev`.

For example, choose `flat` and enter `5` to add five percentage points to every score, with adjusted values clamped to 0–100. Scores 81, 64, and 96 become 86, 69, and 100.

**Use `zscore`, without a hyphen.** The current menu displays `z-score`, but the implementation accepts `zscore`. Curve names are normalized to lowercase by the CLI.

This workflow only produces an assignment-results CSV. It does not update enrolled students, course grades, or GPAs.

## Sample CSV Format

The application uses different CSV layouts for student imports, roster exports, and assignment curving. They are not interchangeable. Both input readers skip the first line, so include a header.

### Student and Course Import: `students.csv`

```csv
StudentID,Name,GradeLevel,StudentType,ClassCode1,Grade1,ClassCode2,Grade2
S001,Alex Rivera,10,Regular,MATH101,85,ENG101,95
S002,Jordan Lee,11,AP,MATH101,90,SCI101,70
```

- The first four fields are student ID, name, numeric grade level, and student type.
- After those fields, provide zero or more pairs of course code and numeric grade.
- `AP` is matched without regard to case; other type values create Regular students.
- Grades should be finite numbers from 0 to 100. A `GradeRecord` constructed with an out-of-range value substitutes 0 rather than rejecting the row.
- Use simple fields without embedded commas, quoted multiline values, or comma-containing names. The parser splits on commas and removes quote characters; it is not a full CSV parser.
- Re-importing a student does not update their existing name, grade level, or type. It can add further course/grade data.

### Roster Export: `roster_export.csv`

For the sample student input, the roster summary is:

```csv
StudentID,Name,GradeLevel,StudentType,GPA
S001,Alex Rivera,10,Regular,3.60
S002,Jordan Lee,11,AP,4.00
```

This layout contains a GPA summary rather than course/grade pairs. It is not a round-trip version of the student-import format.

### Assignment Curver Input: `scores.csv`

```csv
StudentID,Score
S001,81
S002,64
S003,96
```

Each data row must contain exactly two comma-separated fields. The IDs do not have to exist in the student roster. Original assignment scores are not range-validated when read; use finite numbers from 0 to 100 to avoid invalid mathematical results.

### Assignment Curver Output: `curved_scores.csv`

Using the sample above with `flat` and a value of `5`:

```csv
StudentID,OriginalScore,CurvedScore
S001,81.00,86.00
S002,64.00,69.00
S003,96.00,100.00
```

Scores are formatted to two decimal places. Numeric output uses Java's default locale; these examples assume a locale with a decimal point.

## Grade Curving Options

Let `s` be the original score and `v` the numeric parameter entered at the prompt. The following formulas describe the current implementation before adjusted scores are clamped to 0–100.

| Keyword | Formula or behavior | Parameter |
| --- | --- | --- |
| `sqrt` | `sqrt(s) * 10` | None |
| `log` | `25 * ln(s + 1)` | None; `ln` is the natural logarithm |
| `exp` | `(e^(s / 25) - 1) * 20` | None |
| `power` | `(s / 100)^v * 100` | Exponent; e.g. `0.5` raises scores between 0 and 100, while `2` lowers them |
| `sigmoid` | `100 / (1 + e^(-0.1 * (s - 50)))` | None |
| `stddev` | `s - mean + v` | Target mean; shifts scores without rescaling their spread before clamping |
| `zscore` | `((s - mean) / stdDev) * 10 + 75` | None; targets mean 75 and standard deviation 10 before clamping |
| `ratio` | Sort descending and assign 95, 85, 75, 65, or 50 by rank bucket | None |
| `flat` | `s + v` | Points to add; a negative value subtracts points |

Additional implementation details:

- Standard deviation is calculated using the population formula, dividing the squared-deviation sum by the number of scores.
- The code stops both `stddev` and `zscore` processing when standard deviation is zero. Although a simple mean shift could mathematically handle identical scores, the current shared guard rejects that case.
- The rank buckets allocate `floor(n * 0.10)`, `floor(n * 0.20)`, `floor(n * 0.40)`, and `floor(n * 0.20)` students to scores 95, 85, 75, and 65. All remaining students receive 50. Small groups can therefore have empty buckets, and ties are not guaranteed the same adjusted score.
- Clamping can change the resulting mean or standard deviation. These transformations do not all raise every student's score.
- Empty inputs and unknown curve keywords print an error. If a nonempty score list reaches the save step after an unknown curve or a zero-variance rejection, the CLI can still save the original scores and print its completion message.

## Notes

### Data and Error-Handling Boundaries

- All student, course, enrollment, and grade state is held in memory and is lost when the process exits. There is no database or automatic session persistence.
- Student IDs and enrollments have duplicate checks, but grade records do not. Importing the same file again appends grades and can affect GPA calculations, particularly if values change or only some records are repeated.
- Imports are not transactional. A parsing error can stop processing after earlier rows have already been applied; the assignment reader can similarly return scores collected before an error.
- Rows with too few student fields, or assignment rows with a field count other than two, are skipped. Header text itself is not validated.
- Output writers overwrite an existing destination file. Choose a different path from the input file and preserve copies of data you need.
- Error messages are printed to the terminal. Some invalid input can still raise uncaught exceptions, and a completion message is not a substitute for inspecting the exported file.

### Development and Verification

The repository currently has no automated test suite or Maven/Gradle build configuration. The setup commands compile the source directly. A useful manual check is to run the sample import, inspect both GPAs and the four enrollments, export the roster, and run the flat-curve example separately.

Possible future improvements include a full CSV parser, validated imports, consistent failure reporting, grade-update semantics, complete persistence, and automated tests for GPA calculations and curve edge cases. These are improvement ideas, not features implemented in the current version.
