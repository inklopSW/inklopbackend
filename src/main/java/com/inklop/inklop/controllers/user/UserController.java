package com.inklop.inklop.controllers.user;

import com.inklop.inklop.controllers.user.request.BusinessRequest;
import com.inklop.inklop.controllers.user.request.LoginRequest;
import com.inklop.inklop.controllers.user.response.LoginResponse;
import com.inklop.inklop.controllers.user.request.ChangePasswordRequest;
import com.inklop.inklop.controllers.user.request.CreatorRequest;
import com.inklop.inklop.services.BusinessService;
import com.inklop.inklop.services.CreatorService;
import com.inklop.inklop.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.inklop.inklop.controllers.user.response.WalletResponse;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService uS;
    private final CreatorService creatorService;
    private final BusinessService businessService;

    @PostMapping("/register/creator")
    @Operation(
            summary = "Crear un usuario CREATOR",
            description = "Crea un usuario CREATOR. Si ya existe, devolverá error"
    )
    public ResponseEntity<LoginResponse> createFullCreator(@RequestBody CreatorRequest request) {
        LoginResponse creator = creatorService.createFullCreator(request);
        return ResponseEntity.ok(creator);
    }

    @PostMapping("/register/business")
    @Operation(
            summary = "Crear un usuario BUSINESS",
            description = "Crea un usuario BUSINESS. Si ya existe, devolverá error"
    )
    public ResponseEntity<LoginResponse>  createFullBusiness(@RequestBody BusinessRequest request) {
        LoginResponse business = businessService.createFullBusiness(request);
        return ResponseEntity.ok(business);
    }

    @PostMapping("/login")
    @Operation(
            summary = "Login con credenciales",
            description = "Al lograr loguearte con las credenciales(email y contraseña) de un usuario en específico, te responderá con sus datos. Si las credenciales son incorrectas, lanzará error."
    )
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = uS.login(request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/wallet/{idUser}")
    public ResponseEntity<WalletResponse> getWallet(@PathVariable Long idUser) {
        WalletResponse response = uS.getWallet(idUser);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("/change/password/{id}")
    public ResponseEntity<Boolean> changePassword(@PathVariable Long id, @RequestBody ChangePasswordRequest request) {
        Boolean response = uS.changePassword(id, request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    
    @PutMapping(value = "/change/image/{id}", consumes = "multipart/form-data")
    public ResponseEntity<String> changeImage(@PathVariable Long id, @RequestParam("file") MultipartFile file) throws IOException {
        return ResponseEntity.ok(uS.changeImage(id, file));
    }
} 
