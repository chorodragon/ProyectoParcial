// src/main/java/app/dao/CategoriaDAO.java
package app.dao;

import app.db.Conexion;
import app.model.Categoria;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoriaDAO {

    public int insertar(Categoria c) throws SQLException {
        String sql = "INSERT INTO categoria (nombre, estado) VALUES (?, ?)";
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, c.getNombre());
            ps.setInt(2, c.getEstado());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    c.setId(id);
                    return id;
                }
            }
        }
        return -1;
    }

    public List<Categoria> listar() throws SQLException {
        String sql = "SELECT id, nombre, estado FROM categoria ORDER BY id DESC";
        List<Categoria> lista = new ArrayList<>();

        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(new Categoria(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getInt("estado")
                ));
            }
        }
        return lista;
    }

    public List<Categoria> listarActivas() throws SQLException {
        String sql = "SELECT id, nombre, estado FROM categoria WHERE estado = 1 ORDER BY nombre";
        List<Categoria> lista = new ArrayList<>();

        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(new Categoria(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getInt("estado")
                ));
            }
        }
        return lista;
    }

    public Categoria buscarPorId(int id) throws SQLException {
        String sql = "SELECT id, nombre, estado FROM categoria WHERE id = ?";
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Categoria(
                            rs.getInt("id"),
                            rs.getString("nombre"),
                            rs.getInt("estado")
                    );
                }
            }
        }
        return null;
    }

    public boolean actualizar(Categoria c) throws SQLException {
        String sql = "UPDATE categoria SET nombre = ?, estado = ? WHERE id = ?";
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, c.getNombre());
            ps.setInt(2, c.getEstado());
            ps.setInt(3, c.getId());

            return ps.executeUpdate() > 0;
        }
    }

    public boolean eliminarLogico(int id) throws SQLException {
        String sql = "UPDATE categoria SET estado = 0 WHERE id = ?";
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }
}