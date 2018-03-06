/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proyectofinal;

import java.sql.*;
import java.util.logging.Level;

/**
 *
 * @author juancarlos
 */
public class Conector {
    private String database;
    private Connection conector;
    private String sql;

    public Conector(String database) {
        this.database = database;
    }
    
    public void conectar(){
        try {
            conector = DriverManager.getConnection("jdbc:sqlite:"+database);
            if(conector!=null){
                System.out.println("Conectado");
            }
        } catch (SQLException e) {
            System.out.println("No se ha podido conectar a la base de datos");
            System.out.println(e.getMessage());
        }
    }
    
    public void crearTablas(){
        try {
            Statement s = conector.createStatement();
            
            sql = "CREATE TABLE Departamento ("
                    + "IdDpto      INTEGER PRIMARY KEY DEFAULT 1,"
                    + "Usuario     TEXT,"
                    + "Password    TEXT)";
            s.execute(sql);
            
            sql = "CREATE TABLE Noticia ("
                    + "IdDpto   INTEGER REFERENCES Departamento(IdDpto)"
                        + "ON DELETE CASCADE ON UPDATE CASCADE,"
                    + "Imagen   BLOB,"
                    + "PRIMARY KEY(IdDpto))";
            s.execute(sql);
            
            System.out.println("Tablas creadas");
        } catch (SQLException e) {
            System.out.println("Error en la creación de las tablas");
            System.out.println(e.getMessage());
        }
    }
    
    public void cerrar(){
        try {
            conector.close();
            System.out.println("Conexión cerrada");
        } catch (SQLException ex) {
            System.out.println("Error al cerrar la conexión");
            System.out.println(ex.getMessage());
        }
    }
}
