package app.model;

public class Categoria {
    private Integer id;
    private String nombre;
    private int estado;

    public Categoria() {}

    public Categoria(Integer id, String nombre, int estado) {
        this.id = id;
        this.nombre = nombre;
        this.estado = estado;
    }

    public Categoria(String nombre, int estado) {
        this(null, nombre, estado);
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public int getEstado() { return estado; }
    public void setEstado(int estado) { this.estado = estado; }

}