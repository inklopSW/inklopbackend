package com.inklop.inklop.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.inklop.inklop.controllers.files.response.UploadedFileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FileService {

    private final Cloudinary cloudinary;

    public UploadedFileResponse uploadFile(MultipartFile file) {
        try {
            // 1. Obtener nombre original y extensión
            String originalFilename = file.getOriginalFilename();
            String extension = "";

            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String resourceType = getResourceType(file.getContentType());
            String publicId = System.currentTimeMillis() + "";

            if (resourceType.equals("raw")){
                resourceType="auto";
                publicId = publicId + extension;
            }
            
            // 4. Subir a Cloudinary
            Map uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "resource_type", resourceType,
                            "public_id", publicId
                    )
            );

            // 5. Devolver DTO con la información
            return new UploadedFileResponse(
                    originalFilename,
                    extension,
                    uploadResult.get("secure_url").toString(),
                    uploadResult.get("public_id").toString()
            );

        } catch (IOException e) {
            throw new RuntimeException("Error uploading file to Cloudinary", e);
        }
    }

    private String getResourceType(String contentType) {
        if (contentType == null) return "raw";

        if (contentType.startsWith("image")) return "image";
        if (contentType.startsWith("audio") || contentType.startsWith("video")) return "video";

        // PDF, ZIP y otros documentos → raw
        return "raw";
    }

    public String uploadImgeString(MultipartFile file) throws IOException { 
        String resourceType = getResourceType(file.getContentType()); 
        Map uploadResult = cloudinary.uploader().upload( 
            file.getBytes(), 
            ObjectUtils.asMap("resource_type", resourceType) 
            ); 
        return uploadResult.get("secure_url").toString(); 
    }
}
