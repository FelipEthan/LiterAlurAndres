package LiteraLurAndres.model;

import jakarta.persistence.*;

@Entity
@lombok.Data
@lombok.AllArgsConstructor
@lombok.NoArgsConstructor
public class Libros {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String titulo;
    @ManyToOne
    @JoinColumn(name = "autor_id")
    private Autor autores;
    private String idiomas;
    private Double numeroDeDescargas;

    @Override
    public String toString() {
        return "Libros{"
                + "id=" + id
                + ", titulo='" + titulo + '\''
                + ", idiomas='" + idiomas + '\''
                + ", numeroDeDescargas=" + numeroDeDescargas
                + '}';
    }
}
