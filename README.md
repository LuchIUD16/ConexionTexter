# ConexionTester

Aplicación de escritorio en Java Swing para probar conexiones a bases de datos mediante JDBC.

## Archivo
- `ConexionTester.java`: código fuente de la aplicación.


## Requisitos
- JDK instalado.
- El controlador JDBC correspondiente al motor de base de datos. Para SQLite, agregar el JAR de SQLite JDBC al proyecto.

## Ejecución
Compilar y ejecutar `ConexionTester.java` desde el IDE o desde la terminal. La URL inicial es `jdbc:sqlite:inventario.db`.

La aplicación permite ingresar la URL, el usuario y la contraseña, y muestra el resultado de la prueba de conexión.
