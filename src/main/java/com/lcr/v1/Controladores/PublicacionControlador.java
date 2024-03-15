package com.lcr.v1.Controladores;

import com.lcr.v1.Entidades.Publicacion;
import com.lcr.v1.Errores.MyException;
import com.lcr.v1.Servicios.PublicacionServicio;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/publicacion")
@CrossOrigin("*")
public class PublicacionControlador {

    @Autowired
    private PublicacionServicio ps;

    @GetMapping
    public ResponseEntity<List<Publicacion>> index() throws MyException {
        try {
            List<Publicacion> publicaciones = ps.obtenerPublicacionesDestacadas();
            if (publicaciones == null || publicaciones.isEmpty()) {
                return ResponseEntity.status(400).body(null);
            }
            return ResponseEntity.status(200).body(publicaciones);
        } catch (Exception e) {
            return ResponseEntity.status(400).body(null);
        }
    }

    @PostMapping()
    public ResponseEntity<String> subirPublicacion(@RequestParam("titulo") String titulo,
            @RequestParam("precio") Integer precio,
            @RequestParam("descripcion") String descripcion,
            @RequestParam("categoria") String categoria,
            @RequestParam("fotos") List<MultipartFile> fotos) throws MyException {
        try {
            ps.registrarPublicacion(titulo, precio, descripcion, categoria, fotos);
            return ResponseEntity.ok("Publicación subida exitosamente.");
        } catch (MyException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/one/{id}")
    public ResponseEntity<Publicacion> getOne(@PathVariable("id") String id) throws MyException {
        try {
            Publicacion publicacion = ps.getOne(id);
            if (publicacion == null) {
                return ResponseEntity.ok(new Publicacion());
            }
            return ResponseEntity.ok(publicacion);
        } catch (Exception e) {
            return ResponseEntity.ok(new Publicacion());
        }
    }

    @PostMapping("/editar/{id}")
    public ResponseEntity<String> editarPublicacion(@PathVariable("id") String id,
            @RequestParam("titulo") String titulo,
            @RequestParam("precio") Integer precio,
            @RequestParam("descripcion") String descripcion,
            @RequestParam("categoria") String categoria) throws MyException {
        try {
            ps.editar(id, titulo, descripcion, precio, categoria);
            return ResponseEntity.ok("Publicación subida exitosamente.");
        } catch (MyException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/eliminar/{id}")
    public ResponseEntity<String> eliminarPublicacion(@PathVariable("id") String id) throws MyException {
        try {
            ps.eliminarPublicacionPorId(id);
            return ResponseEntity.ok("Publicación eliminada exitosamente.");
        } catch (MyException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/destacadaA/{id}")
    public ResponseEntity<String> agregarDestacada(@PathVariable("id") String id) throws Exception {
        try {
            ps.agregarDestacada(id);
            return ResponseEntity.ok("Publicación agregada a destacadas.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/destacadaE/{id}")
    public ResponseEntity<String> eliminarDestacada(@PathVariable("id") String id) throws Exception {
        try {
            ps.eliminarDestacada(id);
            return ResponseEntity.ok("Publicación eliminada de destacadas.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/categoria")
    public ResponseEntity<List<Publicacion>> listarxCategoria(@RequestParam("categoria") String categoria) throws MyException {
        try {
            List<Publicacion> publicaciones = ps.publicacionesxCategoria(categoria);
            if (publicaciones.isEmpty()) {
                return ResponseEntity.noContent().build(); // Devuelve 204 No Content si no se encuentran publicaciones.
            }
            return ResponseEntity.ok(publicaciones);
        } catch (MyException e) {
            return ResponseEntity.badRequest().build(); // Devuelve 400 Bad Request si ocurre una excepción de validación.
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build(); // Devuelve 400 Bad Request si la categoría es inválida.
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // Devuelve 500 Internal Server Error para otros errores.
        }
    }

    @GetMapping("/buscar/{consulta}")
    public ResponseEntity<List<Publicacion>> busquedaPersonalizada(@PathVariable("consulta") String consulta) {
        try {
            List<Publicacion> publicaciones = ps.busquedaPersonalizada(consulta);
            if (publicaciones.isEmpty()) {
                return ResponseEntity.status(400).body(null);
            }
            return ResponseEntity.ok(publicaciones);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // Devuelve 500 Internal Server Error para otros errores.
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<Publicacion>> findAll() throws MyException {
        try {
            List<Publicacion> publicaciones = ps.listarPublicaciones();
            if (publicaciones == null || publicaciones.isEmpty()) {
                return ResponseEntity.status(400).body(null);
            }
            return ResponseEntity.status(200).body(publicaciones);
        } catch (Exception e) {
            return ResponseEntity.status(400).body(null);
        }
    }

}
