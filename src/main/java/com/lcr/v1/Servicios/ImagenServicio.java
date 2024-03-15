package com.lcr.v1.Servicios;

import com.lcr.v1.Entidades.Imagen;
import com.lcr.v1.Errores.MyException;
import com.lcr.v1.Repositorios.ImagenRepositorio;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ImagenServicio {

    @Autowired
    private ImagenRepositorio ir;

    @Transactional
    public Imagen guardar(MultipartFile archivo) throws MyException {

        archivo = validar(archivo);

        if (archivo != null) {
            try {
                Imagen imagen = new Imagen();

                imagen.setMime(archivo.getContentType());
                imagen.setNombre(archivo.getName());
                imagen.setContenido(archivo.getBytes());

                return ir.save(imagen);

            } catch (Exception e) {

                throw new MyException("No se pudo guardar la imagen");
            }
        }

        return null;
    }

    @Transactional
    public Imagen actualizar(MultipartFile archivo, String id) throws MyException {

        archivo = validar(archivo);

        if (archivo != null) {

            try {

                Imagen imagen = new Imagen();

                if (id != null) {

                    Optional<Imagen> respuesta = ir.findById(id);

                    if (respuesta.isPresent()) {
                        imagen = respuesta.get();
                    }
                }

                imagen.setMime(archivo.getContentType());
                imagen.setNombre(archivo.getName());
                imagen.setContenido(archivo.getBytes());

                return ir.save(imagen);

            } catch (Exception e) {
                throw new MyException("No se pudo actualizar la imagen");
            }
        }

        if (id != null) {
            ir.deleteById(id);
        }

        return null;
    }

    @Transactional
    public List<Imagen> guardarLista(List<MultipartFile> archivos) throws MyException {

        archivos = validarLista(archivos);

        List<Imagen> imagenesGuardadas = new ArrayList<>();

        for (MultipartFile archivo : archivos) {
            archivo = validar(archivo);

            if (archivo != null) {
                try {
                    Imagen imagen = new Imagen();

                    imagen.setMime(archivo.getContentType());
                    imagen.setNombre(archivo.getName());
                    imagen.setContenido(archivo.getBytes());

                    imagenesGuardadas.add(ir.save(imagen));
                } catch (Exception e) {
                    throw new MyException("No se pudo guardar una o varias imágenes");
                }
            }
        }

        return imagenesGuardadas;
    }

    public Imagen getOne(String id) {
        return ir.getOne(id);
    }

    public MultipartFile validar(MultipartFile archivo) {
        if (archivo != null && archivo.getContentType().contains("octet")) {
            return null;
        }

        return archivo;
    }

    public List<MultipartFile> validarLista(List<MultipartFile> archivo) throws MyException {
        if (archivo.isEmpty() || archivo == null) {
            throw new MyException("Debes subir al menos una foto.");
        }
        for (MultipartFile multipartFile : archivo) {
            if (multipartFile != null && multipartFile.getContentType().contains("octet")) {
                throw new MyException("Debe subir una imagen.");
            }
        }
        return archivo;
    }
}
