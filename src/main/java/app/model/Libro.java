// src/main/java/app/model/Libro.java (Actualizado)
package app.model;

public class Libro {
    private Integer id;
    private String nombre;
    private int anio;
    private int idAutor;
    private int idCategoria;
    private int estado;

    public Libro() {}

    public Libro(Integer id, String nombre, int anio, int idAutor, int idCategoria, int estado) {
        this.id = id;
        this.nombre = nombre;
        this.anio = anio;
        this.idAutor = idAutor;
        this.idCategoria = idCategoria;
        this.estado = estado;
    }

    public Libro(String nombre, int anio, int idAutor, int idCategoria, int estado) {
        this(null, nombre, anio, idAutor, idCategoria, estado);
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public int getAnio() { return anio; }
    public void setAnio(int anio) { this.anio = anio; }

    public int getIdAutor() { return idAutor; }
    public void setIdAutor(int idAutor) { this.idAutor = idAutor; }

    public int getIdCategoria() { return idCategoria; }
    public void setIdCategoria(int idCategoria) { this.idCategoria = idCategoria; }

    public int getEstado() { return estado; }
    public void setEstado(int estado) { this.estado = estado; }

    @Override
    public String toString() {
        return "Libro{id=" + id + ", nombre='" + nombre + "'}";
    }
}