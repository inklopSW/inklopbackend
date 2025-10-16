package com.inklop.inklop.controllers.chat;

import com.inklop.inklop.controllers.chat.request.EditMessageRequest;
import com.inklop.inklop.controllers.chat.request.EditRoomRequest;
import com.inklop.inklop.controllers.chat.request.MessageRequestTwo;
import com.inklop.inklop.controllers.chat.response.BothRoomResponse;
import com.inklop.inklop.controllers.chat.response.FullChatResponse;
import com.inklop.inklop.services.MessageService;
import com.inklop.inklop.services.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {

    private final RoomService roomService;
    private final MessageService messageService;

    /*
     * @PostMapping
    @Operation(
            summary = "Crear un Room",
            description = "Crea un Room para una campaña específica. Si ya existe, devuelve error."
    )
    public RoomRequest createRoom(@RequestBody CrearChatRequest request, @RequestParam Long userId) {
        return roomService.createRoom(request,userId);
    }

    @PostMapping("/canal-anuncio")
    @Operation(
            summary = "Crear un Room",
            description = "Crea un Room de anuncio para una campaña específica. Si ya existe, devuelve error."
    )
    public RoomRequest createRoomAnuncio(@RequestBody CrearChatRequest request, @RequestParam Long userId) {
        return roomService.createCanalAnuncio(request,userId);
    }

    @GetMapping("/{roomId}")
    @Operation(
            summary = "Obtener información de un Room",
            description = "Verifica la existencia de un Room y devuelve su información básica."
    )
    public RoomRequest joinRoom(@PathVariable Long roomId) {
        return roomService.joinRoom(roomId);
        
@GetMapping("/{roomId}/messages")
    @Operation(
            summary = "Listar mensajes de un Room",
            description = "Obtiene una lista paginada de mensajes del Room."
    )
    public List<MessageRequest> getMessages(
            @PathVariable Long roomId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return roomService.getMessages(roomId, page, size);
    }
    }
     */

    

    @GetMapping("/room/user/{userId}")
    @Operation(
        summary = "Ver los rooms",
        description = "Ver los rooms de un usuario"
    )
    public ResponseEntity<List<BothRoomResponse>> getAllRoms(@PathVariable Long userId){
        return ResponseEntity.ok(roomService.getRooms(userId));
    }

    @DeleteMapping("/room/{roomId}")
    @Operation(
        summary = "Eliminar un Room",
        description = "Elimina un Room si la campaña asociada ha terminado y el usuario es el Business creador")
    public ResponseEntity<Boolean> deleteRoom(@PathVariable Long roomId) {
        return ResponseEntity.ok(roomService.deleteRoomTwo(roomId));
    }

    @PutMapping("/room/{roomId}")
    @Operation(
        summary = "Editar un Room",
        description = "Edita un Roomd}")
    public ResponseEntity<Boolean> editRoom(@PathVariable Long roomId, @RequestBody EditRoomRequest request) {
        return ResponseEntity.ok(roomService.editRoom(roomId, request));
    }


    @GetMapping("/messages/room/{roomId}")
    @Operation(
        summary = "Ver los mensajes",
        description = "Ver los mensajes de un room")
    public ResponseEntity<List<FullChatResponse>> getMessages(@PathVariable Long roomId){
        return ResponseEntity.ok(messageService.getMessages(roomId));
    }

    @GetMapping("/messages/room/{roomId}/before")
    public ResponseEntity<List<FullChatResponse>> getMessagesBefore(
            @PathVariable Long roomId,
            @RequestParam("before") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime before,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
                return ResponseEntity.ok(messageService.getMessagesBefore(roomId, before, limit));
            }


    @PostMapping("/messages")
    @Operation(
        summary = "Enviar un mensaje",
        description = "Permite a un usuario autenticado enviar un mensaje al Room. Ingresa el id_token")
    public ResponseEntity<FullChatResponse> sendMessage(MessageRequestTwo request){
        return ResponseEntity.ok(messageService.sendMessageTwo(request));
    }

    @PutMapping("/messages/{messageId}")
    @Operation( 
        summary = "Editar un mensaje",
        description = "Permite al autor editar su propio mensaje. Pero te tienes que ingresar el id_token")
    public ResponseEntity<Boolean> editMessage(@PathVariable Long messageId, @RequestBody EditMessageRequest request) {
        return ResponseEntity.ok(messageService.editMessageTwo(messageId, request));
    }

    @DeleteMapping("/messages/{messageId}")
    @Operation(
            summary = "Eliminar un mensaje",
            description = "Permite al autor eliminar su propio mensaje. Ingresa para autenticarte el id_token"
    )
    public ResponseEntity<Boolean> deleteMessage(@PathVariable Long messageId){
        return ResponseEntity.ok(messageService.deleteMessageTwo(messageId));
    }
}
