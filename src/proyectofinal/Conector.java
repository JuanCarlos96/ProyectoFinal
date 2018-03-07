/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proyectofinal;

import com.mysql.jdbc.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author juancarlos
 */
public class Conector {
    private Connection conexion;
    private String sql;
    private Statement stmnt;
    private final String URL = "jdbc:mysql://localhost/noticias";
    private final String USER = "root";
    private final String PASSWORD = "root";

    public Conector() {
        try{
            Class.forName("com.mysql.jdbc.Driver");
            this.conexion = (Connection) DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Base de datos conectada.");
        } catch (ClassNotFoundException | SQLException ex) {
            System.out.println("Error cargando el driver");
            System.out.println(ex.getMessage());
        }
    }
    
    public void crearTablas() {
        try {
            stmnt = conexion.createStatement();
            
            sql = "CREATE TABLE IF NOT EXISTS Departamento ("
                    + "IdDpto   INTEGER DEFAULT 1,"
                    + "Usuario  VARCHAR(30),"
                    + "Clave    VARCHAR(30),"
                    + "PRIMARY KEY(IdDpto))";
            stmnt.execute(sql);
            
            sql = "CREATE TABLE IF NOT EXISTS Noticia("
                    + "IdNot    INTEGER AUTO_INCREMENT,"
                    + "IdDpto   INTEGER REFERENCES Departamento(IdDpto)"
                        + "ON DELETE CASCADE ON UPDATE CASCADE,"
                    + "Imagen   LONGBLOB,"
                    + "Fecha    DATE,"// Formato: YYYY-MM-DD
                    + "PRIMARY KEY(IDNot))";
            stmnt.execute(sql);
            
            sql = "INSERT INTO Departamento (Usuario, Clave) VALUES ('admin', 'admin')";// Creación del usuario Administrador
            stmnt.execute(sql);
            
            System.out.println("Tablas creadas");
        } catch (SQLException e) {
            System.out.println("Error en la creación de las tablas");
            System.out.println(e.getMessage());
        }
    }
    
    public void reiniciarBD() {
        try {
            stmnt = conexion.createStatement();
            
            sql = "DROP TABLE Noticia";
            stmnt.execute(sql);
            
            sql = "DROP TABLE Departamento";
            stmnt.execute(sql);
            
            System.out.println("Tablas borradas");
            crearTablas();
        } catch (SQLException e) {
            System.out.println("Error al reiniciar la base de datos");
            System.out.println(e.getMessage());
        }
    }
    
    public void cerrar() {
        if (conexion!=null) {
            try {
                conexion.close();
                System.out.println("Conexión cerrada.");
            } catch (SQLException e) {
                System.out.println("No se pudo cerrar la conexión.");
                System.out.println(e.getMessage());
            }
        }
    }
}
