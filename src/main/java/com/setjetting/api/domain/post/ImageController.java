package com.setjetting.api.domain.post;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/uploads")
@Tag(name = "이미지 서빙")
public class ImageController {

    @Value("${project.upload.path}")
    private String uploadPath;

    @GetMapping("/{filename:.+}")
    @Operation(summary = "업로드된 이미지 조회")
    public ResponseEntity<Resource> serveImage(@PathVariable String filename) {
        try {
            Path filePath = Paths.get(uploadPath).resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                return ResponseEntity.notFound().build();
            }

            MediaType mediaType = MediaTypeFactory.getMediaType(filename)
                    .orElse(MediaType.APPLICATION_OCTET_STREAM);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CACHE_CONTROL, "max-age=86400")
                    .contentType(mediaType)
                    .body(resource);

        } catch (MalformedURLException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
