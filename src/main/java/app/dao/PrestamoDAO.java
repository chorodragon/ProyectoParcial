// src/main/java/app/dao/PrestamoDAO.java
package app.dao;

import app.db.Conexion;
import app.model.Prestamo;
import app.model.PrestamoDetalle;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PrestamoDAO {

    public int insertar(Prestamo p) throws SQLException {
        String sql = "INSERT INTO prestamo (idCliente, idLibro, fecha, estado) VALUES (?, ?, ?, ?)";
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, p.getIdCliente());
            ps.setInt(2, p.getIdLibro());
            ps.setDate(3, p.getFecha());
            ps.setInt(4, p.getEstado());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    p.setId(id);
                    return id;
                }
            }
        }
        return -1;
    }

    public List<PrestamoDetalle> listarConDetalles() throws SQLException {
        String sql = """
                SELECT p.id, c.nombre AS clienteNombre, l.nombre AS libroNombre, p.fecha, p.estado
                FROM prestamo p
                JOIN cliente c ON c.id = p.idCliente
                JOIN libro l ON l.id = p.idLibro
                ORDER BY p.id DESC
                """;
        List<PrestamoDetalle> lista = new ArrayList<>();

        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(new PrestamoDetalle(
                        rs.getInt("id"),
                        rs.getString("clienteNombre"),
                        rs.getString("libroNombre"),
                        rs.getDate("fecha"),
                        rs.getInt("estado")
                ));
            }
        }
        return lista;
    }

    public List<PrestamoDetalle> listarActivos() throws SQLException {
        String sql = """
                SELECT p.id, c.nombre AS clienteNombre, l.nombre AS libroNombre, p.fecha, p.estado
                FROM prestamo p
                JOIN cliente c ON c.id = p.idCliente
                JOIN libro l ON l.id = p.idLibro
                WHERE p.estado = 1
                ORDER BY p.fecha DESC
                """;
        List<PrestamoDetalle> lista = new ArrayList<>();

        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(new PrestamoDetalle(
                        rs.getInt("id"),
                        rs.getString("clienteNombre"),
                        rs.getString("libroNombre"),
                        rs.getDate("fecha"),
                        rs.getInt("estado")
                ));
            }
        }
        return lista;
    }

    public Prestamo buscarPorId(int id) throws SQLException {
        String sql = "SELECT id, idCliente, idLibro, fecha, estado FROM prestamo WHERE id = ?";
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Prestamo(
                            rs.getInt("id"),
                            rs.getInt("idCliente"),
                            rs.getInt("idLibro"),
                            rs.getDate("fecha"),
                            rs.getInt("estado")
                    );
                }
            }
        }
        return null;
    }

    public boolean actualizar(Prestamo p) throws SQLException {
        String sql = "UPDATE prestamo SET idCliente = ?, idLibro = ?, fecha = ?, estado = ? WHERE id = ?";
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, p.getIdCliente());
            ps.setInt(2, p.getIdLibro());
            ps.setDate(3, p.getFecha());
            ps.setInt(4, p.getEstado());
            ps.setInt(5, p.getId());

            return ps.executeUpdate() > 0;
        }
    }

    public boolean devolverPrestamo(int id) throws SQLException {
        String sql = "UPDATE prestamo SET estado = 0 WHERE id = ?";
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean libroEstaPrestado(int idLibro) throws SQLException {
        String sql = "SELECT COUNT(*) FROM prestamo WHERE idLibro = ? AND estado = 1";
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idLibro);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    public List<PrestamoDetalle> listarPorCliente(int idCliente) throws SQLException {
        String sql = """
                SELECT p.id, c.nombre AS clienteNombre, l.nombre AS libroNombre, p.fecha, p.estado
                FROM prestamo p
                JOIN cliente c ON c.id = p.idCliente
                JOIN libro l ON l.id = p.idLibro
                WHERE p.idCliente = ?
                ORDER BY p.fecha DESC
                """;
        List<PrestamoDetalle> lista = new ArrayList<>();

        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idCliente);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new PrestamoDetalle(
                            rs.getInt("id"),
                            rs.getString("clienteNombre"),
                            rs.getString("libroNombre"),
                            rs.getDate("fecha"),
                            rs.getInt("estado")
                    ));
                }
            }
        }
        return lista;
    }

    public List<PrestamoDetalle> listarPorLibro(int idLibro) throws SQLException {
        String sql = """
                SELECT p.id, c.nombre AS clienteNombre, l.nombre AS libroNombre, p.fecha, p.estado
                FROM prestamo p
                JOIN cliente c ON c.id = p.idCliente
                JOIN libro l ON l.id = p.idLibro
                WHERE p.idLibro = ?
                ORDER BY p.fecha DESC
                """;
        List<PrestamoDetalle> lista = new ArrayList<>();

        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idLibro);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new PrestamoDetalle(
                            rs.getInt("id"),
                            rs.getString("clienteNombre"),
                            rs.getString("libroNombre"),
                            rs.getDate("fecha"),
                            rs.getInt("estado")
                    ));
                }
            }
        }
        return lista;
    }
}