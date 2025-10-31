package app.cliente;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
// tabla cliente con los indices indicados
@Table (name = "cliente", indexes ={
        //indice sobre ciudad para facilitar la busqueda de este como SELECT WHERE ciudad = ?
        @Index(name = "idx_cliente_ciudad", columnList = "ciudad"),
        //y este indice es mas que todo para email y asi evitar registros duplicados por algun correo
        @Index(name = "idx_cliente_email", columnList = "email", unique = true)
})
public class Cliente {
    // constructor por defecto requerido para el jpa
    public Cliente() {}


    // campos de la entidad y sus anotaciones

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    // columnas para la bd

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String apellidos;

    @Column(nullable = false, length = 1)
    private String sexo;

    @Column(nullable = false)
    private String ciudad;

    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;

    @Column(nullable = false)
    private String telefono;

    @Column(nullable = false, unique = true)
    private String email;

    //constructor con argumentos para el formato de altas


    public Cliente(String nombre, String apellidos, String sexo, String ciudad, LocalDate fechaNacimiento, String telefono, String email) {
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.sexo = sexo;
        this.ciudad = ciudad;
        this.fechaNacimiento = fechaNacimiento;
        this.telefono = telefono;
        this.email = email;
    }

    // getters y setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "\u001B[32mCliente ID: \u001B[0m" + id + "\n" +
                "nombre: " + nombre + "\n" +
                "apellidos: " + apellidos + "\n" +
                "sexo: " + sexo + "\n" +
                "ciudad: " + ciudad + "\n" +
                "fechaNacimiento: " + fechaNacimiento + "\n" +
                "telefono: " + telefono + "\n" +
                "email: " + email + "\n";
    }
}
