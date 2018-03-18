/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proyectofinal;

import com.mysql.jdbc.Connection;
import java.awt.HeadlessException;
import java.io.*;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
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
    private PreparedStatement pstmnt;
    private final String USER = "root";
    private final String PASSWORD = "pi2018..";

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
                    + "Ruta     VARCHAR(80)"// Ruta donde se guardará la foto en el equipo
                    + "DiasVigencia INTEGER,"
                    + "Vigente  BOOLEAN,"
                    + "Publica  BOOLEAN,"// Campo del visto bueno (checkbox)
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
    
    public void addDepartamento(Departamento d) {
        try {
            sql = "INSERT INTO Departamento VALUES (?, ?, ?)";
            pstmnt = conexion.prepareStatement(sql);
            pstmnt.setInt(1, d.getId());
            pstmnt.setString(2, d.getUsuario());
            pstmnt.setString(3, d.getClave());
            pstmnt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error creando el usuario");
            e.printStackTrace();
        }
    }
    
    public void modDepartamento(Departamento d) {
        try {
            sql = "UPDATE Departamento SET Usuario=?, Clave=? WHERE IdDpto=?";
            pstmnt = conexion.prepareStatement(sql);
            pstmnt.setString(1, d.getUsuario());
            pstmnt.setString(2, d.getClave());
            pstmnt.setInt(3, d.getId());
            pstmnt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error modificando el usuario");
            e.printStackTrace();
        }
    }
    
    public void eliminarDepartamento(int id) {
        try {
            sql = "DELETE FROM Departamento WHERE IdDpto=?";
            pstmnt = conexion.prepareStatement(sql);
            pstmnt.setInt(1, id);
            pstmnt.executeUpdate();
        } catch (HeadlessException | SQLException e) {
            System.out.println("Error eliminando el usuario");
            e.printStackTrace();
        }
    }
    
    public int getMaxId() {
        int maxId = 0;
        try {
            sql = "SELECT MAX(IdDpto) FROM Departamento";
            stmnt = conexion.createStatement();
            ResultSet rs = stmnt.executeQuery(sql);
            
            while (rs.next()) {
                maxId = rs.getInt("MAX(IdDpto)")+1;
            }
            rs.close();
        } catch (SQLException e) {
            System.out.println("Error obteniendo el máximo ID");
            e.printStackTrace();
        }
        return maxId;
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
                // Creación del fichero con el usuario administrador
                DataOutputStream dos = new DataOutputStream(new FileOutputStream(new File("administrador.dat")));
                stmnt = conexion.createStatement();
                sql = "SELECT * FROM Departamento WHERE IdDpto=1";
                ResultSet rs = stmnt.executeQuery(sql);
                while(rs.next()) {
                    dos.writeUTF(Integer.toString(rs.getInt("IdDpto"))+" "+rs.getString("Usuario")+" "+rs.getString("Clave"));
                }
                rs.close();
                dos.close();
                
                // Creación del fichero con los usuarios normales
                stmnt = conexion.createStatement();
                sql = "SELECT * FROM Departamento WHERE IdDpto>1";
                ResultSet rs2 = stmnt.executeQuery(sql);
                if (rs2.next()==true) {
                    rs2.beforeFirst();
                    DataOutputStream dos2 = new DataOutputStream(new FileOutputStream(new File("usuarios.dat")));
                    while(rs2.next()) {
                        dos2.writeUTF(Integer.toString(rs2.getInt("IdDpto"))+" "+rs2.getString("Usuario")+" "+rs2.getString("Clave"));
                    }
                    rs2.close();
                    dos2.close();
                }else {
                    File usuarios = new File("usuarios.dat");
                    if (usuarios.exists()) {
                        usuarios.delete();
                    }
                    System.out.println("No hay usuarios en la BD");
                }
                
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
