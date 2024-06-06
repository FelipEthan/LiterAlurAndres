package LiteraLurAndres.repository;

import LiteraLurAndres.model.Libros;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LibrosRepo extends JpaRepository<Libros, Long> {

    Libros save(Libros libro);


    @Query("SELECT l FROM Libros l WHERE l.idiomas = :idioma")
    List<Libros> findByIdioma(@Param("idioma") String idioma);

//    @Query("SELECT l FROM Libros l WHERE l.idiomas IS NOT EMPTY")
//    List<Libros> findByIdiomas();
}
