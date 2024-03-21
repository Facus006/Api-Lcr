package com.lcr.v1.Servicios;

import com.lcr.v1.Enums.Categoria;
import com.lcr.v1.Entidades.Imagen;
import com.lcr.v1.Entidades.Publicacion;
import com.lcr.v1.Errores.MyException;
import com.lcr.v1.Repositorios.PublicacionRepositorio;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class PublicacionServicio {

    @Autowired
    ImagenServicio imagenServicio;
    @Autowired
    private PublicacionRepositorio publicacionRepo;

// Valida que los valores no estén vacíos o nulos.
    private void validar(String titulo, Integer precio, String descripcion, String categoria) throws MyException {
        if (titulo.isEmpty() || descripcion == null) {
            throw new MyException("Ingrese un título");
        }
        if (precio == 0 || precio < 0) {
            throw new MyException("Ingrese un precio válido");
        }
        if (descripcion.isEmpty() || descripcion == null) {
            throw new MyException("La descripción no puede estar vacía.");
        }
        if (categoria.isEmpty() || categoria == null || categoria.equals("VACIO")) {
            throw new MyException("La categoría no puede estar vacía.");
        }
        if (categoria.equals("VACIO")) {
            throw new MyException("La categoría no puede estar vacía.");
        }
    }

// Valida que tenga al menos una foto.
    public void validarFotos(List<MultipartFile> fotos) throws MyException {
        if (fotos.isEmpty() || fotos == null) {
            throw new MyException("Debes subir al menos una foto.");
        }
    }

    // Este servicio es para registrar una publicación.
    @Transactional
    public void registrarPublicacion(String titulo, Integer precio, String descripcion, String categoria,
            List<MultipartFile> fotos) throws MyException {

        validar(titulo, precio, descripcion, categoria); // En estos validamos utilizando los métodos anteriores.
        validarFotos(fotos);
        List<MultipartFile> primeras5Fotos = fotos.stream().limit(5).collect(Collectors.toList()); // En esta línea limitamos las fotos a 5 como máximo.

        Categoria categoriaEnum = Categoria.valueOf(categoria.toUpperCase()); // En este convertimos el String de categoría que recibimos como parámetro y lo convertimos en un enum.

        Publicacion publi = new Publicacion(); // Acá creamos un objeto publicación y le seteamos los atributos que recibimos como parámetro.
        Calendar calendar = Calendar.getInstance(); // Este lo utilizamos para setear la fecha del día de hoy, para saber cuándo se subió la publicación.
        Date fecha = calendar.getTime();
        publi.setTitulo(titulo);
        publi.setPrecio(precio);
        publi.setFechaPublicacion(fecha);
        publi.setDescripcion(descripcion);
        publi.setCategoria(categoriaEnum);
        publi.setDestacada(false);

        List<Imagen> fotosGuardadas = imagenServicio.guardarLista(primeras5Fotos); // Acá llamamos al servicio de imagen para guardar las 5 fotos.
        publi.setFotos(fotosGuardadas); // Acá las seteamos.
        publicacionRepo.save(publi); // Y por último llamamos al repositorio y guardamos.
    }

// Este método sirve para editar una publicación.
    @Transactional
    public void editar(String id, String titulo, String descripcion, Integer precio, String categoria) throws MyException {
        validar(titulo, precio, descripcion, categoria); // Primero validamos que los atributos no sean nulos.
        Categoria categoriaEnum = Categoria.valueOf(categoria.toUpperCase()); // Convertimos el string en un enum.
        Optional<Publicacion> publicacionOptional = publicacionRepo.findById(id); // Buscamos la publicación a editar a través de su id.
        if (publicacionOptional.isPresent()) { // Nos aseguramos de que la publicación se encontró.
            Publicacion publi = publicacionOptional.get(); // El repositorio nos devuelve un Optional y aquí lo convertimos en un objeto publicación.
            publi.setTitulo(titulo); // Seteamos los atributos.
            publi.setDescripcion(descripcion);
            publi.setPrecio(precio);
            publi.setCategoria(categoriaEnum);
            publicacionRepo.save(publi); // Y guardamos los cambios en el repositorio.
        } else {
            throw new MyException("No se encontró la publicación."); // Si la publicación no está presente lanzará una Exception.
        }
    }

// Este método es para eliminar una publicación.
    @Transactional
    public void eliminarPublicacionPorId(String idPublicacion) throws MyException {

        Optional<Publicacion> respuesta = publicacionRepo.findById(idPublicacion); // Primero buscamos la publicación por id.
        if (respuesta.isPresent()) {
            publicacionRepo.deleteById(idPublicacion); // Validamos que esté presente y llamamos al repositorio para eliminar la publicación.
        }
    }

    // Este método es para listar las publicaciones y se ordenan por fecha de subida.
    @Transactional(readOnly = true)
    public List<Publicacion> listarPublicaciones() {

        List<Publicacion> publicaciones = publicacionRepo.findAll(); // Llamamos al repositorio para que traiga todas las publicaciones.
        // Comparator<Publicacion> comparadorSubida = (p1, p2) -> p2.getFechaPublicacion().compareTo(p1.getFechaPublicacion()); // Luego hacemos un comparador para comparar las fechas y que se ordenen de la más reciente a la más antigua.
        // publicaciones.sort(comparadorSubida);
        return publicaciones;
    }

// Este es para obtener una publicación por su id.
    public Publicacion getOne(String id) throws MyException {
        Publicacion publicacion = publicacionRepo.getOne(id);
        if (publicacion == null) {
            throw new MyException("Publicación no encontrada con ID: " + id);
        }
        return publicacion;
    }

    public List<Publicacion> publicacionesxCategoria(String categoria) throws MyException {
        try {
            Categoria categoriaEnum = Categoria.valueOf(categoria.toUpperCase());
            List<Publicacion> publicaciones = publicacionRepo.buscarPorCategoria(categoriaEnum);
            if (publicaciones.isEmpty()) {
                return new ArrayList<>();
            }
            return publicaciones;
        } catch (IllegalArgumentException e) {
            throw new MyException("Categoría no encontrada");
        }
    }
// En este método es para buscar las publicaciones destacadas y las buscamos a través de una Query.

    @Transactional(readOnly = true)
    public List<Publicacion> obtenerPublicacionesDestacadas() {
        return publicacionRepo.obtenerPublicacionesDestacadas();
    }

// Este es para eliminar una publicación destacada.
    @Transactional
    public void eliminarDestacada(String id) {
        publicacionRepo.findById(id).ifPresent(publicacion -> { // Primero buscamos si existe esa publicación y si existe la convertimos a un objeto de manera más fácil.
            publicacion.setDestacada(false);
            publicacionRepo.save(publicacion); // Seteamos y guardamos en el repositorio.
        });
    }

// Este es para agregar una destacada y es lo mismo que el anterior pero seteamos en true.
    @Transactional
    public void agregarDestacada(String id) throws Exception {
        try {
            publicacionRepo.findById(id).ifPresent(publicacion -> {
                publicacion.setDestacada(true);
                publicacionRepo.save(publicacion);
            });
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }

    }

    /* Este servicio es para hacer una búsqueda personalizada de mods */
    @Transactional(readOnly = true)
    public List<Publicacion> busquedaPersonalizada(String consulta) {

        List<Publicacion> publicaciones = publicacionRepo.findAll(); // Acá traemos todas las publicaciones.
        List<Publicacion> resultados = new ArrayList<>(); // Creamos una nueva lista para guardar las que contengan la consulta.

        for (Publicacion publicacion : publicaciones) { // Iteramos con un foreach.
            String titulo = publicacion.getTitulo();
            String descripcion = publicacion.getDescripcion();
            String categoria = publicacion.getCategoria().toString(); // Obtenemos los atributos que queremos comparar con la consulta

            // Verifica si la consulta está contenida en el título, descripción o categoría (ignorando mayúsculas y minúsculas).
            if (titulo.toUpperCase().contains(consulta.toUpperCase()) || descripcion.toUpperCase().contains(consulta.toUpperCase()) || categoria.toUpperCase().contains(consulta.toUpperCase())) {
                resultados.add(publicacion); // Si la consulta está contenida en el título, descripción o la categoría se guarda en la nueva lista.
            }
        }
        // Y aquí ordenamos la lista de la consulta por precio (primero las más baratas)
        return resultados;
    }
}
