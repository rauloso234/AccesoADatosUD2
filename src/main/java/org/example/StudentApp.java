package org.example;



import java.sql.*;
import java.util.Scanner;

public class StudentApp {

    // Configura tus propias credenciales y detalles de la base de datos
    public static void main(String[] args) {
        try {
            // Cargar el controlador JDBC (opcional en versiones recientes de Java)
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }

        try {
            // Establecer la conexión a la base de datos
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/academia", "root", "");

            // Crea la tabla si no existe
            createTableIfNotExists(connection);

            Scanner scanner = new Scanner(System.in);

            // Menú de opciones
            while (true) {
                System.out.println("Selecciona una opción:");
                System.out.println("1. Mostrar estudiantes");
                System.out.println("2. Agregar estudiante");
                System.out.println("3. Actualizar estudiante");
                System.out.println("4. Eliminar estudiante");
                System.out.println("5. Buscar estudiantes");
                System.out.println("6. Salir");

                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume el salto de línea

                switch (choice) {
                    case 1:
                        displayStudents(connection);
                        break;
                    case 2:
                        addStudent(connection, scanner);
                        break;
                    case 3:
                        updateStudent(connection, scanner);
                        break;
                    case 4:
                        deleteStudent(connection, scanner);
                        break;
                    case 5:
                        searchStudents(connection, scanner);
                        break;
                    case 6:
                        System.out.println("Saliendo...");
                        System.exit(0);
                        break;
                    default:
                        System.out.println("Opción no válida. Inténtalo de nuevo.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Crea la tabla si no existe
    private static void createTableIfNotExists(Connection connection) throws SQLException {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS students (" + "id INT AUTO_INCREMENT PRIMARY KEY,"
                + "Nombre VARCHAR(255) NOT NULL," + "Apellido VARCHAR(255) NOT NULL," + "Edad INT,"
                + "Curso VARCHAR(50)" + ")";
        Statement statement = connection.createStatement();
        statement.execute(createTableSQL);
    }

    // Muestra la información de los estudiantes
    private static void displayStudents(Connection connection) throws SQLException {
        String query = "SELECT * FROM students";
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(query);

        while (resultSet.next()) {
            int id = resultSet.getInt("id");
            String name = resultSet.getString("Nombre");
            String apellido = resultSet.getString("Apellido");
            int age = resultSet.getInt("Edad");
            String grade = resultSet.getString("Curso");

            System.out.println("Id: " + id + ", Nombre: " + name + ", Apellido: " + apellido + ", Edad: " + age
                    + ", Curso: " + grade);
        }
    }

    // Agrega un nuevo estudiante
    private static void addStudent(Connection connection, Scanner scanner) throws SQLException {
        System.out.println("Ingrese el nombre del estudiante:");
        String name = scanner.nextLine();

        System.out.println("Ingrese el apellido del estudiante:"); // Nuevo campo "apellido"
        String lastName = scanner.nextLine();

        System.out.println("Ingrese la edad del estudiante:");
        int age = scanner.nextInt();
        scanner.nextLine(); // Consume el salto de línea

        System.out.println("Ingrese el curso del estudiante:");
        String grade = scanner.nextLine();

        // Sentencia SQL preparada para evitar la inyección de SQL
        String insertSQL = "INSERT INTO students (Nombre, Apellido, Edad, Curso) VALUES (?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(insertSQL,
                Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, lastName);
            preparedStatement.setInt(3, age);
            preparedStatement.setString(4, grade);

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Estudiante agregado con éxito.");

                // Obtener el ID generado automáticamente
                ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int generatedId = generatedKeys.getInt(1);
                    System.out.println("ID generado para el estudiante: " + generatedId);
                } else {
                    System.out.println("No se pudo obtener el ID generado.");
                }
            } else {
                System.out.println("No se pudo agregar el estudiante.");
            }
        }
    }

    // Actualiza la información de un estudiante
    private static void updateStudent(Connection connection, Scanner scanner) throws SQLException {
        System.out.println("Ingrese el ID del estudiante que desea actualizar:");
        int id = scanner.nextInt();
        scanner.nextLine(); // Consume el salto de línea

        System.out.println("Ingrese el nuevo nombre del estudiante:");
        String name = scanner.nextLine();

        System.out.println("Ingrese el nuevo Apellido del Estudiante:");
        String apellido = scanner.nextLine();

        System.out.println("Ingrese la nueva edad del estudiante:");
        int age = scanner.nextInt();
        scanner.nextLine(); // Consume el salto de línea

        System.out.println("Ingrese la nueva calificación del estudiante:");
        String grade = scanner.nextLine();

        // Sentencia SQL preparada para evitar la inyección de SQL
        String updateSQL = "UPDATE students SET Nombre = ?, Edad = ?, Curso = ?, Apellido = ? WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(updateSQL)) {
            preparedStatement.setString(1, name);
            preparedStatement.setInt(2, age);
            preparedStatement.setString(3, grade);
            preparedStatement.setString(4, apellido);
            preparedStatement.setInt(5, id);

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Estudiante actualizado con éxito.");
            } else {
                System.out.println("No se encontró ningún estudiante con ese ID.");
            }
        }
    }

    // Elimina un estudiante
    private static void deleteStudent(Connection connection, Scanner scanner) throws SQLException {
        System.out.println("Ingrese el ID del estudiante que desea eliminar:");
        int id = scanner.nextInt();
        scanner.nextLine(); // Consume el salto de línea

        // Sentencia SQL preparada para evitar la inyección de SQL
        String deleteSQL = "DELETE FROM students WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(deleteSQL)) {
            preparedStatement.setInt(1, id);

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Estudiante eliminado con éxito.");
            } else {
                System.out.println("No se encontró ningún estudiante con ese ID.");
            }
        }
    }

    // Modifica el método displayStudents para aceptar un parámetro de búsqueda
    private static void mostarEstudianteConcreto(Connection connection, String searchType, String searchTerm)
            throws SQLException {
        String query;
        if ("ID".equals(searchType)) {
            query = "SELECT * FROM students WHERE id = ?";
        } else if ("Nombre".equals(searchType)) {
            query = "SELECT * FROM students WHERE Nombre = ?";
        } else if ("Curso".equals(searchType)) {
            query = "SELECT * FROM students WHERE Curso = ?";
        } else {
            query = "SELECT * FROM students";
        }

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            if (!"Todos".equals(searchType)) {
                preparedStatement.setString(1, searchTerm);
            }

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("Nombre");
                int age = resultSet.getInt("Edad");
                String grade = resultSet.getString("Curso");

                System.out.println("Id: " + id + ", Nombre: " + name + ", Edad: " + age + ", Curso: " + grade);
            }
        }
    }

    private static void searchStudents(Connection connection, Scanner scanner) throws SQLException {
        System.out.println("Seleccione el tipo de búsqueda:");
        System.out.println("1. ID");
        System.out.println("2. Nombre");
        System.out.println("3. Apellido");
        System.out.println("4. Curso");
        System.out.println("5. Mostrar todos");
        int searchChoice = scanner.nextInt();
        scanner.nextLine();

        String searchType;
        String searchTerm;

        switch (searchChoice) {
            case 1:
                searchType = "ID";
                System.out.println("Ingrese el ID del estudiante:");
                searchTerm = scanner.nextLine();
                break;
            case 2:
                searchType = "Nombre";
                System.out.println("Ingrese el nombre del estudiante:");
                searchTerm = scanner.nextLine();
                break;
            case 3:
                searchType = "Apellido";
                System.out.println("Ingrese el apellido del estudiante:"); // Nuevo campo "apellido"
                searchTerm = scanner.nextLine();
                break;
            case 4:
                searchType = "Curso";
                System.out.println("Ingrese el curso del estudiante:");
                searchTerm = scanner.nextLine();
                break;
            default:
                searchType = "Todos";
                searchTerm = "";
        }

        mostarEstudianteConcreto(connection, searchType, searchTerm);
    }
}