package com.inklop.inklop.controllers.files.response;

public record UploadedFileResponse(
    String originalName,
    String extension,
    String url,
    String publicId
) {
    
}
