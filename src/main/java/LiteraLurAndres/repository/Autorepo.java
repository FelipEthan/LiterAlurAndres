package LiteraLurAndres.repository;

import LiteraLurAndres.model.Autor;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface Autorepo extends JpaRepository<Autor, Long> {

    Autor save(Autor libro);

    Autor findByNombre(String nombre);

    @Query("SELECT a FROM Autor a WHERE a.fechaDeNacimiento = :años")
    List<Autor> findAutoresVivosEnAño(String años);
}
