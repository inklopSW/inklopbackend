package com.inklop.inklop.controllers.chat.response;

import java.util.List;

public record BothRoomResponse(
    String avatarCampaign,
    String CampaignName,
    String namesOfRoom,
    List<FullRoomResponse> rooms
) {
}
