package com.inklop.inklop.controllers.files;

import com.inklop.inklop.controllers.files.response.UploadedFileResponse;
import com.inklop.inklop.services.FileService;
import com.inklop.inklop.services.S3Service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;
    private final S3Service s3Service;

    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
    try {
        UploadedFileResponse uploadedFileResponse = fileService.uploadFile(file);
        return ResponseEntity.ok(uploadedFileResponse);
    } catch (Exception e) {
        return ResponseEntity.status(500).body(
                 java.util.Map.of("error", "Upload failed", "details", e.getMessage())
        );
    }
    }

    @PostMapping(value = "/upload/s3", consumes = "multipart/form-data")
    public ResponseEntity<?> uploadFileS3(@RequestParam("file") MultipartFile file) {
    try {
        String url= s3Service.uploadFile(file);
        return ResponseEntity.ok().body(url);
    } catch (Exception e) {
        return ResponseEntity.status(500).body(
                 java.util.Map.of("error", "Upload failed", "details", e.getMessage())
        );
    }
}

}
