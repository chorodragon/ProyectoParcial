package app.model;

public class Cliente {
    private Integer id;
    private String nombre;
    private String nit;
    private String telefono;
    private int estado;

    public Cliente() {}

    public Cliente(Integer id, String nombre, String nit, String telefono, int estado) {
        this.id = id;
        this.nombre = nombre;
        this.nit = nit;
        this.telefono = telefono;
        this.estado = estado;
    }

    public Cliente(String nombre, String nit, String telefono, int estado) {
        this(null, nombre, nit, telefono, estado);
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getNit() { return nit; }
    public void setNit(String nit) { this.nit = nit; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public int getEstado() { return estado; }
    public void setEstado(int estado) { this.estado = estado; }

}