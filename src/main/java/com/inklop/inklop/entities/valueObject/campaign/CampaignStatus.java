package com.inklop.inklop.entities.valueObject.campaign;

public enum CampaignStatus {
    PENDING, 
    IN_COMING, // esperando fechas - aprobado
    IN_PROGRESS, // acutal - aprobado
    COMPLETED, // acabado - aprobado
    CANCELLED, // cancelado por el usuaio
    REJECTED, // rechzado
    DELETED, // eliminado
    BANNED
}
