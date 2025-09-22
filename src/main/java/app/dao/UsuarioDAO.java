package app.dao;

import app.core.PasswordUtil;
import app.db.Conexion;
import app.model.Usuario;

import java.io.IOException;
import java.sql.*;
import java.text.ParseException;

public class UsuarioDAO {

    /** Crea un usuario: guarda el HASH en la columna 'password' */
    public int crearUsuario(String username, String plainPassword, String nombre, String rol) throws SQLException {
        String sql = "INSERT INTO usuario (username, password, nombre, rol, estado) VALUES (?,?,?,?,1)";
        String hash = PasswordUtil.hash(plainPassword);

        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, username);
            ps.setString(2, hash);      // guardamos el HASH en 'password'
            ps.setString(3, nombre);
            ps.setString(4, rol);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                return rs.next() ? rs.getInt(1) : -1;
            }
        }
    }

    /** Valida login: compara plain con el HASH guardado en 'password' */
    public Usuario validarLogin(String username, String plainPassword) throws SQLException {
        String sql = "SELECT id, username, password, nombre, rol, estado " +
                "FROM usuario WHERE username=? AND estado=1";
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;

                String hash = rs.getString("password");
                boolean ok = PasswordUtil.verify(plainPassword, hash);
                if (!ok) return null;

                return new Usuario(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("nombre"),
                        rs.getString("rol"),
                        rs.getInt("estado")
                );
            }
        }
    }
}