package com.inklop.inklop.services;


import com.inklop.inklop.controllers.chat.request.EditRoomRequest;
import com.inklop.inklop.controllers.chat.response.BothRoomResponse;
import com.inklop.inklop.controllers.chat.response.FullRoomResponse;
import com.inklop.inklop.entities.Room;
import com.inklop.inklop.entities.User;
import com.inklop.inklop.entities.Campaign.Campaign;
import com.inklop.inklop.entities.valueObject.chat.ChatStatus;
import com.inklop.inklop.entities.valueObject.chat.RoomType;
import com.inklop.inklop.entities.valueObject.user.UserRole;
import com.inklop.inklop.repositories.RoomRepository;
import com.inklop.inklop.repositories.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class RoomService {
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;

    
    @Transactional
    public Boolean createBothRooms(Campaign campaign){
        Room room = Room.builder()
                .campaign(campaign)
                .title(campaign.getName()+"- GENERAL")
                .type(RoomType.GENERAL)
                .photo(campaign.getLogo())
                .createdAt(LocalDateTime.now())
                .build();

        Room room2 = Room.builder()
                .campaign(campaign)
                .title(campaign.getName()+"- CANAL_ANUNCIO")
                .type(RoomType.CANAL_ANUNCIO)
                .photo("https://res.cloudinary.com/dfweegj8i/image/upload/v1758660069/w9pa0lghvrs2qczpk6qp.png")
                .createdAt(LocalDateTime.now())
                .build();
        
        roomRepository.save(room);
        roomRepository.save(room2);

        return true;
    }

    public List<BothRoomResponse> getRooms(Long id){
        User user = userRepository.findById(id).get();
        List<Room> rooms = new ArrayList<>();
        if (user.getUserRole().equals(UserRole.BUSINESS)){
            rooms.addAll(roomRepository.findRoomsForBusinessUser(id));
        }else if (user.getUserRole().equals(UserRole.CREATOR)){
            rooms.addAll(roomRepository.findRoomsForAcceptedUser(id));
        }

        Map<Long, List<Room>> roomsByCampaign = rooms.stream()
                .collect(Collectors.groupingBy(r -> r.getCampaign().getId()));

        return roomsByCampaign.values().stream()
            .map(roomList -> {
                Campaign campaign = roomList.get(0).getCampaign(); // todas son de la misma campaña
                List<FullRoomResponse> fullRooms = roomList.stream()
                        .map(room -> new FullRoomResponse(
                                room.getId(),
                                campaign.getId(),
                                room.getPhoto(),
                                campaign.getBusiness().getBusinessName(),
                                room.getTitle(),
                                room.getDescription(),
                                room.getType(),
                                room.getStatus(),
                                room.getCreatedAt(),
                                room.getUpdatedAt()
                        ))
                        .toList();
                String namesOfRoom = roomList.stream()
                        .map(room -> room.getTitle())
                        .collect(Collectors.joining(", "));

                return new BothRoomResponse(
                        campaign.getLogo(),
                        campaign.getName(),
                        namesOfRoom, // ejemplo de "namesOfRoom"
                        fullRooms
                );
            })
            .toList();
    }

    public Boolean editRoom(Long id, EditRoomRequest request){
        Room room= roomRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Room no encontrado"));
        room.setTitle(request.name());
        room.setDescription(request.description());
        roomRepository.save(room);
        return true;
    } 

    public Boolean deleteRoomTwo(Long id){
        Room room= roomRepository.findById(id).get();
        room.setStatus(ChatStatus.DELETED);
        roomRepository.save(room);
        return true;
    }

    /**
     * Obtener mensajes paginados.
     * Transactional
    public void deleteRoom(Long roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new EntityNotFoundException("Room no encontrado"));

        Campaign campaign = room.getCampaign();

        if (campaign.getEndDate().isAfter(LocalDate.now())) {
            throw new RuntimeException("No se puede eliminar el room: la campaña aún está activa");
        }

        if (!campaign.getBusiness().getUser().getId().equals(userId)) {
            throw new RuntimeException("No autorizado: solo el Business que creó la campaña puede eliminar este room");
        }

        roomRepository.delete(room);

        public List<MessageRequest> getMessages(Long roomId, int page, int size) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new EntityNotFoundException("Room no encontrado"));

        List<Message> allMessages = messageRepository.findByChatRoomId(roomId);
        allMessages.sort((m1, m2) -> m1.getSentAt().compareTo(m2.getSentAt()));

        int start = Math.max(0, allMessages.size() - (page + 1) * size);
        int end = Math.min(allMessages.size(), start + size);

        List<Message> paginated = allMessages.subList(start, end);

        return paginated.stream()
                .map(msg -> new MessageRequest(
                        msg.getId(),
                        msg.getUser().getId(),
                        msg.getContent(),
                        msg.getUser().getUserRole(),
                        msg.getSentAt())
                )
                .collect(Collectors.toList());
    }
    }
     */
    

    /**
     * Validar si un usuario puede enviar mensaje en un room.
     * En CANAL_ANUNCIO solo el Business de la campaña puede enviar.
     */
   
         /**
     * Crea un room general para la campaña (chat normal).
     * Transactional
    public RoomRequest createRoom(CrearChatRequest request, Long userId) {
        Campaign campaign = campaignRepository.findById(request.campaignId())
                .orElseThrow(() -> new EntityNotFoundException("Campaña no encontrada"));

        if (!campaign.getBusiness().getUser().getId().equals(userId)) {
            throw new RuntimeException("No autorizado: solo el Business que creó la campaña puede crear el room");
        }

        if (roomRepository.findByCampaignIdAndType(campaign.getId(), RoomType.GENERAL).isPresent()) {
            throw new RuntimeException("Ya existe un chat general para esta campaña");
        }

        Room room = Room.builder()
                .campaign(campaign)
                .type(RoomType.GENERAL)
                .createdAt(LocalDateTime.now())
                .build();

        Room savedRoom = roomRepository.save(room);

        return new RoomRequest(
                savedRoom.getId(),
                campaign.getId(),
                savedRoom.getCreatedAt()
        );
    }

    Transactional
    public RoomRequest createCanalAnuncio(CrearChatRequest request, Long userId) {
        Campaign campaign = campaignRepository.findById(request.campaignId())
                .orElseThrow(() -> new EntityNotFoundException("Campaña no encontrada"));

        if (!campaign.getBusiness().getUser().getId().equals(userId)) {
            throw new RuntimeException("No autorizado: solo el Business que creó la campaña puede crear el canal de anuncio");
        }

        if (roomRepository.findByCampaignIdAndType(campaign.getId(), RoomType.CANAL_ANUNCIO).isPresent()) {
            throw new RuntimeException("Ya existe un canal de anuncio para esta campaña");
        }

        Room room = Room.builder()
                .campaign(campaign)
                .type(RoomType.CANAL_ANUNCIO)
                .createdAt(LocalDateTime.now())
                .build();

        Room savedRoom = roomRepository.save(room);

        return new RoomRequest(
                savedRoom.getId(),
                campaign.getId(),
                savedRoom.getCreatedAt()
        );

        public RoomRequest joinRoom(Long roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new EntityNotFoundException("Room no encontrado"));

        Campaign campaign = room.getCampaign();

        return new RoomRequest(
                room.getId(),
                campaign.getId(),
                room.getCreatedAt()
        );

        public boolean canUserSendMessage(Long roomId, Long userId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new EntityNotFoundException("Room no encontrado"));

        if (room.getType() == RoomType.GENERAL) {
            return true;
        }

        if (room.getType() == RoomType.CANAL_ANUNCIO) {
            return room.getCampaign()
                    .getBusiness()
                    .getUser()
                    .getId()
                    .equals(userId);
        }

        return false;
    }
    }
    }

     */

}
