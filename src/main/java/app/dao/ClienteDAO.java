// src/main/java/app/dao/ClienteDAO.java
package app.dao;

import app.db.Conexion;
import app.model.Cliente;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {

    public int insertar(Cliente c) throws SQLException {
        String sql = "INSERT INTO cliente (nombre, nit, telefono, estado) VALUES (?, ?, ?, ?)";
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, c.getNombre());
            ps.setString(2, c.getNit());
            ps.setString(3, c.getTelefono());
            ps.setInt(4, c.getEstado());
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

    public List<Cliente> listar() throws SQLException {
        String sql = "SELECT id, nombre, nit, telefono, estado FROM cliente ORDER BY id DESC";
        List<Cliente> lista = new ArrayList<>();

        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(new Cliente(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("nit"),
                        rs.getString("telefono"),
                        rs.getInt("estado")
                ));
            }
        }
        return lista;
    }

    public List<Cliente> listarActivos() throws SQLException {
        String sql = "SELECT id, nombre, nit, telefono, estado FROM cliente WHERE estado = 1 ORDER BY nombre";
        List<Cliente> lista = new ArrayList<>();

        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(new Cliente(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("nit"),
                        rs.getString("telefono"),
                        rs.getInt("estado")
                ));
            }
        }
        return lista;
    }

    public Cliente buscarPorId(int id) throws SQLException {
        String sql = "SELECT id, nombre, nit, telefono, estado FROM cliente WHERE id = ?";
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Cliente(
                            rs.getInt("id"),
                            rs.getString("nombre"),
                            rs.getString("nit"),
                            rs.getString("telefono"),
                            rs.getInt("estado")
                    );
                }
            }
        }
        return null;
    }

    public boolean actualizar(Cliente c) throws SQLException {
        String sql = "UPDATE cliente SET nombre = ?, nit = ?, telefono = ?, estado = ? WHERE id = ?";
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, c.getNombre());
            ps.setString(2, c.getNit());
            ps.setString(3, c.getTelefono());
            ps.setInt(4, c.getEstado());
            ps.setInt(5, c.getId());

            return ps.executeUpdate() > 0;
        }
    }

    public boolean eliminarLogico(int id) throws SQLException {
        String sql = "UPDATE cliente SET estado = 0 WHERE id = ?";
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    public List<Cliente> buscarPorNombre(String nombre) throws SQLException {
        String sql = "SELECT id, nombre, nit, telefono, estado FROM cliente WHERE nombre LIKE ? AND estado = 1 ORDER BY nombre";
        List<Cliente> lista = new ArrayList<>();

        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, "%" + nombre + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Cliente(
                            rs.getInt("id"),
                            rs.getString("nombre"),
                            rs.getString("nit"),
                            rs.getString("telefono"),
                            rs.getInt("estado")
                    ));
                }
            }
        }
        return lista;
    }
}