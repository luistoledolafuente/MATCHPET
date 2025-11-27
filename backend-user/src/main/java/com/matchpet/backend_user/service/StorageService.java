package com.matchpet.backend_user.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Path;

public interface StorageService {
    /** Guarda un archivo y devuelve la URL relativa que se almacena en la DB. */
    String store(MultipartFile file);

    /** Carga el archivo como un Resource. */
    Resource loadAsResource(String filename);
}