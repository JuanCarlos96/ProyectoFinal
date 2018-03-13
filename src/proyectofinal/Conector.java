/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proyectofinal;

import com.mysql.jdbc.Connection;
import java.io.*;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import javax.swing.JOptionPane;

/**
 *
 * @author juancarlos
 */
public class Conector {
    private Connection conexion = null;
    private String sql;
    private Statement stmnt;
    private final String USER = "root";
    private final String PASSWORD = "root";

    public Conector(String ip) {
        try{
            Class.forName("com.mysql.jdbc.Driver");
            this.conexion = (Connection) DriverManager.getConnection("jdbc:mysql://"+ip+"/noticias", USER, PASSWORD);
            JOptionPane.showMessageDialog(null, "Base de datos conectada");
            System.out.println("Base de datos conectada.");
        } catch (ClassNotFoundException | SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error conectando con la base de datos");
            System.out.println("Error cargando el driver");
            //ex.printStackTrace();
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
            e.printStackTrace();
        }
    }
    
    public ArrayList<Departamento> listaUsuarios() {
        ArrayList<Departamento> usuarios = new ArrayList();
        try {
            sql = "SELECT * FROM Departamento";
            stmnt = conexion.createStatement();
            ResultSet rs = stmnt.executeQuery(sql);
            
            while(rs.next()) {
                usuarios.add(new Departamento(rs.getInt("IdDpto"), rs.getString("Usuario"), rs.getString("Clave")));
            }
            rs.close();
        } catch (SQLException ex) {
            System.out.println("Error obteniendo los usuarios");
            ex.printStackTrace();
        }
        return usuarios;
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
            e.printStackTrace();
        }
    }
    
    public void cerrar() {
        if (conexion!=null) {
            try {
                DataOutputStream dos = new DataOutputStream(new FileOutputStream(new File("administrador.dat")));
                stmnt = conexion.createStatement();
                sql = "SELECT * FROM Departamento WHERE IdDpto=1";
                ResultSet rs = stmnt.executeQuery(sql);
                while(rs.next()) {
                    dos.writeUTF(Integer.toString(rs.getInt("IdDpto"))+" "+rs.getString("Usuario")+" "+rs.getString("Clave"));
                }
                rs.close();
                dos.close();
                conexion.close();
                System.out.println("Conexión cerrada.");
            } catch (SQLException e) {
                System.out.println("No se pudo cerrar la conexión.");
                e.printStackTrace();
            } catch (IOException ex) {
                System.out.println("Error creando el archivo administrador.dat");
                ex.printStackTrace();
            }
        }
    }

    public Connection getConexion() {
        return conexion;
    }
}
