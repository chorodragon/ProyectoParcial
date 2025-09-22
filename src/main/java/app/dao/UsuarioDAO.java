package app.dao;

import app.core.PasswordUtil;
import app.db.Conexion;
import app.model.Usuario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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

    public List<Usuario> listar() throws SQLException {
        String sql = "SELECT id, username, nombre, rol, estado FROM usuario ORDER BY id DESC";
        List<Usuario> lista = new ArrayList<>();

        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(new Usuario(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("nombre"),
                        rs.getString("rol"),
                        rs.getInt("estado")
                ));
            }
        }
        return lista;
    }

    public Usuario buscarPorId(int id) throws SQLException {
        String sql = "SELECT id, username, nombre, rol, estado FROM usuario WHERE id = ?";
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
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
        return null;
    }

    public boolean actualizarSinPassword(int id, String username, String nombre, String rol, int estado) throws SQLException {
        String sql = "UPDATE usuario SET username = ?, nombre = ?, rol = ?, estado = ? WHERE id = ?";
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, nombre);
            ps.setString(3, rol);
            ps.setInt(4, estado);
            ps.setInt(5, id);

            return ps.executeUpdate() > 0;
        }
    }

    public boolean actualizarConPassword(int id, String username, String nombre, String rol, int estado, String nuevaPassword) throws SQLException {
        String sql = "UPDATE usuario SET username = ?, password = ?, nombre = ?, rol = ?, estado = ? WHERE id = ?";
        String hash = PasswordUtil.hash(nuevaPassword);
        
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, hash);
            ps.setString(3, nombre);
            ps.setString(4, rol);
            ps.setInt(5, estado);
            ps.setInt(6, id);

            return ps.executeUpdate() > 0;
        }
    }

    public boolean eliminarLogico(int id) throws SQLException {
        String sql = "UPDATE usuario SET estado = 0 WHERE id = ?";
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean existeUsername(String username) throws SQLException {
        String sql = "SELECT COUNT(*) FROM usuario WHERE username = ?";
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    public boolean existeUsernameExceptoId(String username, int id) throws SQLException {
        String sql = "SELECT COUNT(*) FROM usuario WHERE username = ? AND id != ?";
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setInt(2, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }
}