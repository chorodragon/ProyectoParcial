package app.model;

import java.sql.Date;

public class Prestamo {
    private Integer id;
    private int idCliente;
    private int idLibro;
    private Date fecha;
    private int estado;

    public Prestamo() {}

    public Prestamo(Integer id, int idCliente, int idLibro, Date fecha, int estado) {
        this.id = id;
        this.idCliente = idCliente;
        this.idLibro = idLibro;
        this.fecha = fecha;
        this.estado = estado;
    }

    public Prestamo(int idCliente, int idLibro, Date fecha, int estado) {
        this(null, idCliente, idLibro, fecha, estado);
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public int getIdCliente() { return idCliente; }
    public void setIdCliente(int idCliente) { this.idCliente = idCliente; }

    public int getIdLibro() { return idLibro; }
    public void setIdLibro(int idLibro) { this.idLibro = idLibro; }

    public Date getFecha() { return fecha; }
    public void setFecha(Date fecha) { this.fecha = fecha; }

    public int getEstado() { return estado; }
    public void setEstado(int estado) { this.estado = estado; }

}