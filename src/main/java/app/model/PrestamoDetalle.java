package app.model;

import java.sql.Date;

public class PrestamoDetalle {
    private final int id;
    private final String clienteNombre;
    private final String libroNombre;
    private final Date fecha;
    private final int estado;

    public PrestamoDetalle(int id, String clienteNombre, String libroNombre, Date fecha, int estado) {
        this.id = id;
        this.clienteNombre = clienteNombre;
        this.libroNombre = libroNombre;
        this.fecha = fecha;
        this.estado = estado;
    }

    public int getId() { return id; }
    public String getClienteNombre() { return clienteNombre; }
    public String getLibroNombre() { return libroNombre; }
    public Date getFecha() { return fecha; }
    public int getEstado() { return estado; }
}