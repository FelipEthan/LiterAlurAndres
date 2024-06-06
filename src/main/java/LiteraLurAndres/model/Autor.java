package LiteraLurAndres.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@lombok.Data
@lombok.AllArgsConstructor
@lombok.NoArgsConstructor
public class Autor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;

    @Column(name = "fecha_de_nacimiento")
    private String fechaDeNacimiento;
    @Column(name = "fecha_de_fallecimiento")
    private String fechaDeFallecimiento;

    @OneToMany(mappedBy = "autores", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Libros> libros;

    public Autor(String nombre, String fechaDeNacimiento, String fechaDeFallecimiento) {
        this.nombre = nombre;
        this.fechaDeNacimiento = fechaDeNacimiento;
        this.fechaDeFallecimiento = fechaDeFallecimiento;
    }

    @Override
    public String toString() {
        return "\nAutor:\n"
                + "id = " + id + "\n"
                + "nombre = " + nombre + "\n"
                + "fechaDeNacimiento = " + fechaDeNacimiento + "\n"
                + "fechaDeFallecimiento = " + fechaDeFallecimiento
                + "\n";
    }
}
