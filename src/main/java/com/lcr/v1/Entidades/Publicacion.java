package com.lcr.v1.Entidades;

import com.lcr.v1.Enums.Categoria;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.util.Date;
import java.util.List;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Publicacion {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;

    private String descripcion;
    private String titulo;
    private Integer precio;

    @OneToMany
    private List<Imagen> fotos;
    @Enumerated(EnumType.STRING)
    private Categoria categoria;
    @Temporal(TemporalType.DATE)
    private Date fechaPublicacion;
    private boolean destacada;

}
