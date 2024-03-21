package com.lcr.v1.Repositorios;

import com.lcr.v1.Enums.Categoria;
import com.lcr.v1.Entidades.Publicacion;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PublicacionRepositorio extends JpaRepository<Publicacion, String> {

    @Query("SELECT p FROM Publicacion p WHERE p.destacada = true")
    List<Publicacion> obtenerPublicacionesDestacadas();

    @Query("SELECT p FROM Publicacion p WHERE p.categoria = :categoria")
    List<Publicacion> buscarPorCategoria(@Param("categoria") Categoria categoria);

}
