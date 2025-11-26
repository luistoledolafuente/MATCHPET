package com.matchpet.backend_user.service.Imp;

import com.matchpet.backend_user.service.StorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileSystemStorageService implements StorageService {

    private final Path rootLocation;
    private final String filesPath = "animales/files/"; // Subdirectorio dentro de uploads

    public FileSystemStorageService(@Value("${app.upload-dir}") String uploadDir) {
        // Combinar el directorio de carga con el subdirectorio de animales
        this.rootLocation = Paths.get(uploadDir).resolve(filesPath);
        try {
            Files.createDirectories(this.rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("No se pudo inicializar el directorio de carga de archivos.", e);
        }
    }

    @Override
    public String store(MultipartFile file) {
        if (file.isEmpty()) {
            throw new RuntimeException("Fallo al guardar archivo vacío.");
        }
        try {
            String originalFilename = file.getOriginalFilename();
            // Asegurarse de que el nombre de archivo no contenga path traversal
            if (originalFilename.contains("..")) {
                throw new RuntimeException("Ruta no válida.");
            }
            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String uniqueFilename = UUID.randomUUID().toString() + fileExtension;

            Path destinationFile = this.rootLocation.resolve(Paths.get(uniqueFilename))
                    .normalize().toAbsolutePath();

            Files.copy(file.getInputStream(), destinationFile);

            // Devuelve la URL relativa que el Controller servirá
            return "/api/animales/files/" + uniqueFilename;

        } catch (IOException e) {
            throw new RuntimeException("Fallo al guardar el archivo.", e);
        }
    }

    @Override
    public Resource loadAsResource(String filename) {
        try {
            // El 'filename' aquí será solo el UUID, ya que el Controller lo extrae
            Path file = rootLocation.resolve(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("No se pudo encontrar el archivo: " + filename);
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error en la URL del archivo: " + filename, e);
        }
    }
}