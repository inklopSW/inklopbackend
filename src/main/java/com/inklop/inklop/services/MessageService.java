package com.inklop.inklop.services;

import com.inklop.inklop.controllers.chat.request.EditMessageRequest;
import com.inklop.inklop.controllers.chat.request.MessageRequestTwo;
import com.inklop.inklop.controllers.chat.response.FullChatResponse;
import com.inklop.inklop.entities.Message;
import com.inklop.inklop.entities.Room;
import com.inklop.inklop.entities.User;
import com.inklop.inklop.entities.valueObject.chat.ChatStatus;
import com.inklop.inklop.entities.valueObject.chat.RoomType;
import com.inklop.inklop.repositories.MessageRepository;
import com.inklop.inklop.repositories.RoomRepository;
import com.inklop.inklop.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

@Service
@Transactional
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;
    ZoneId peruZone = ZoneId.of("America/Lima");



    // Patrón para detectar enlaces prohibidos
    private static final Pattern WHATSAPP_LINK_PATTERN =
            Pattern.compile("(https?://)?(wa\\.me|chat\\.whatsapp\\.com)/", Pattern.CASE_INSENSITIVE);

    // Patrón genérico para detectar cualquier URL
    private static final Pattern URL_PATTERN =
            Pattern.compile("https?://[\\w\\-]+(\\.[\\w\\-]+)+[/#?]?.*", Pattern.CASE_INSENSITIVE);

    // Lista negra de dominios peligrosos xddddddddddddddddddd
    private static final String[] MALICIOUS_DOMAINS = {
            "phishing.com", "malware.com", "virus-site.com"
    };
    
    private void validateMessageContent(String content) {
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("El contenido del mensaje no puede estar vacío");
        }

        if (WHATSAPP_LINK_PATTERN.matcher(content).find()) {
            throw new RuntimeException("No se permiten enlaces de WhatsApp en el chat");
        }

        if (URL_PATTERN.matcher(content).find()) {
            for (String domain : MALICIOUS_DOMAINS) {
                if (content.toLowerCase().contains(domain)) {
                    throw new RuntimeException("Enlace bloqueado por seguridad");
                }
            }
        }
    }

    private String getUsername(User user) {
        if (user.getUserRole() == null) {
            return "Usuario";
        }
        switch (user.getUserRole()) {
            case CREATOR -> {
                return user.getCreator().getUsername();
            }
            case BUSINESS -> {
                return user.getBusiness().getBusinessName();
            }
            default -> {
                return user.getRealName();
            }
        }
    }

    public List<FullChatResponse> getMessages(Long roomId) {
        List<Message> messages = messageRepository.findTop50ByChatRoomIdOrderBySentAtDesc(roomId);
        return messages.stream()
                .map(msg -> new FullChatResponse(
                        msg.getId(),
                        msg.getUser().getAvatarUrl(),
                        getUsername(msg.getUser()),
                        msg.getStatus(),
                        msg.getType(),
                        msg.getContent(),
                        msg.getSentAt().atZone(ZoneId.of("UTC")).withZoneSameInstant(peruZone).toLocalDateTime(),
                        msg.getUpdatedAt().atZone(ZoneId.of("UTC")).withZoneSameInstant(peruZone).toLocalDateTime()
                ))
                .toList();
    }

    public List<FullChatResponse> getMessagesBefore(Long roomId, LocalDateTime before, int pages){
        List<Message> messages = messageRepository.findByChatRoomIdAndSentAtBeforeOrderBySentAtDesc(roomId, before, Pageable.ofSize(pages));
        Collections.reverse(messages);
        return messages.stream()
                .map(msg -> new FullChatResponse(
                        msg.getId(),
                        msg.getUser().getAvatarUrl(),
                        getUsername(msg.getUser()),
                        msg.getStatus(),
                        msg.getType(),
                        msg.getContent(),
                        msg.getSentAt().atZone(ZoneId.of("UTC")).withZoneSameInstant(peruZone).toLocalDateTime(),
                        msg.getUpdatedAt().atZone(ZoneId.of("UTC")).withZoneSameInstant(peruZone).toLocalDateTime()
                ))
                .toList();
    }

    public FullChatResponse sendMessageTwo(MessageRequestTwo request){
        Room room = roomRepository.findById(request.idRoom())
                .orElseThrow(() -> new EntityNotFoundException("Room no encontrado"));

        if (room.getStatus() == ChatStatus.DELETED) {
            throw new RuntimeException("El chat ha sido eliminado");
        }

        if (request.content() == null || request.content().trim().isEmpty()) {
            throw new IllegalArgumentException("El contenido del mensaje no puede estar vacío");
        }

        if (room.getType() == RoomType.CANAL_ANUNCIO) {
            if (!room.getCampaign().getBusiness().getUser().getId().equals(request.idUser())){
                throw new RuntimeException("Solo el propietario (empresa o streamer) que creó la campaña puede enviar mensajes en este canal"); 
            }
        }

        User sender = userRepository.findById(request.idUser())
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        
        validateMessageContent(request.content());

        Message message = Message.builder()
                .chatRoom(room)
                .user(sender)
                .type(request.type())
                .content(request.content())
                .sentAt(LocalDateTime.now())
                .build();

        messageRepository.save(message);

        FullChatResponse response = new FullChatResponse(
                message.getId(),
                sender.getAvatarUrl(),
                getUsername(sender),
                message.getStatus(),
                message.getType(),
                message.getContent(),
                message.getSentAt().atZone(ZoneId.of("UTC")).withZoneSameInstant(peruZone).toLocalDateTime(),
                message.getUpdatedAt().atZone(ZoneId.of("UTC")).withZoneSameInstant(peruZone).toLocalDateTime()
        );

        messagingTemplate.convertAndSend("/topic/chat/"+room.getId(), response);

        return response;
    }

    public Boolean editMessageTwo(Long messageId, EditMessageRequest request){
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Mensaje no encontrado"));

    
        if (request.content() == null || request.content().trim().isEmpty()) {
            throw new IllegalArgumentException("El contenido del mensaje no puede estar vacío");
        }

        // Reaplicar validaciones de seguridad al editar
        validateMessageContent(request.content());

        message.setType(request.type());
        message.setStatus(ChatStatus.EDITED);
        message.setContent(request.content());
        message.setUpdatedAt(LocalDateTime.now());

        messageRepository.save(message);

        FullChatResponse response = new FullChatResponse(
                message.getId(),
                message.getUser().getAvatarUrl(),
                getUsername(message.getUser()),
                message.getStatus(),
                message.getType(),
                message.getContent(),
                message.getSentAt().atZone(ZoneId.of("UTC")).withZoneSameInstant(peruZone).toLocalDateTime(),
                message.getUpdatedAt().atZone(ZoneId.of("UTC")).withZoneSameInstant(peruZone).toLocalDateTime()
        );

        messagingTemplate.convertAndSend("/topic/chat/"+message.getChatRoom().getId(), response);

        return true;
    }



    public Boolean deleteMessageTwo(Long Id){
        Message message = messageRepository.findById(Id)
                .orElseThrow(() -> new RuntimeException("Mensaje no encontrado"));
        message.setStatus(ChatStatus.DELETED);
        messageRepository.save(message);

        messageRepository.save(message);

        FullChatResponse response = new FullChatResponse(
                message.getId(),
                message.getUser().getAvatarUrl(),
                getUsername(message.getUser()),
                message.getStatus(),
                message.getType(),
                message.getContent(),
                message.getSentAt().atZone(ZoneId.of("UTC")).withZoneSameInstant(peruZone).toLocalDateTime(),
                message.getUpdatedAt().atZone(ZoneId.of("UTC")).withZoneSameInstant(peruZone).toLocalDateTime()
        );

        messagingTemplate.convertAndSend("/topic/chat/"+message.getChatRoom().getId(), response);

        return true;
    }

    /**
     * Enviar mensaje.
     * public MessageRequest sendMessage(Long roomId, CrearMessageRequest request, String userEmail) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new EntityNotFoundException("Room no encontrado"));

        User sender = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        String content = request.content();
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("El contenido del mensaje no puede estar vacío");
        }

        if (room.getType() == RoomType.CANAL_ANUNCIO) {
            Long businessOwnerId = room.getCampaign()
                    .getBusiness()
                    .getUser()
                    .getId();

            if (!businessOwnerId.equals(sender.getId())) {
                throw new RuntimeException("Solo el propietario (empresa o streamer) que creó la campaña puede enviar mensajes en este canal");
            }
        }

        // Bloquear enlaces de WhatsApp
        if (WHATSAPP_LINK_PATTERN.matcher(content).find()) {
            throw new RuntimeException("No se permiten enlaces de WhatsApp en el chat");
        }

        // Bloquear dominios maliciosos conocidos
        if (URL_PATTERN.matcher(content).find()) {
            for (String domain : MALICIOUS_DOMAINS) {
                if (content.toLowerCase().contains(domain)) {
                    throw new RuntimeException("Enlace bloqueado por seguridad");
                }
            }
        }

        Message message = Message.builder()
                .chatRoom(room)
                .user(sender)
                .content(content)
                .sentAt(LocalDateTime.now())
                .build();

        Message saved = messageRepository.save(message);

        log.info("Mensaje enviado por {} en sala {}", userEmail, roomId);

        return buildMessageDto(saved);
    }


    

    public MessageRequest editMessage(Long messageId, CrearMessageRequest request, String userEmail) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Mensaje no encontrado"));

        if (!message.getUser().getEmail().equals(userEmail)) {
            throw new RuntimeException("No autorizado para editar este mensaje");
        }

        String content = request.content();
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("El contenido del mensaje no puede estar vacío");
        }

        // Reaplicar validaciones de seguridad al editar
        if (WHATSAPP_LINK_PATTERN.matcher(content).find()) {
            throw new RuntimeException("No se permiten enlaces de WhatsApp en el chat");
        }
        if (URL_PATTERN.matcher(content).find()) {
            for (String domain : MALICIOUS_DOMAINS) {
                if (content.toLowerCase().contains(domain)) {
                    throw new RuntimeException("Enlace bloqueado por seguridad");
                }
            }
        }

        message.setContent(content);
        message.setUpdatedAt(LocalDateTime.now());

        Message updated = messageRepository.save(message);

        log.info("Mensaje editado por usuario {}", userEmail);

        return buildMessageDto(updated);
    }

    public void deleteMessage(Long messageId, String userEmail) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Mensaje no encontrado"));

        if (!message.getUser().getEmail().equals(userEmail)) {
            throw new RuntimeException("No autorizado para eliminar este mensaje");
        }

        messageRepository.delete(message);

        log.info("Mensaje eliminado por usuario {}", userEmail);
    }

    private MessageRequest buildMessageDto(Message message) {
        return new MessageRequest(
                message.getId(),
                message.getUser().getId(),
                message.getContent(),
                message.getUser().getUserRole(),
                message.getSentAt()
        );
    }
     */
    

}
