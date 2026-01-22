package kg.edgepos.authservice.mapper;

import kg.edgepos.authservice.service.MessageService;
import kg.edgepos.authservice.dao.entity.UserEventEntity;
import kg.edgepos.authservice.dto.UserEventDto;
import lombok.experimental.UtilityClass;

@UtilityClass
public class UserEventMapper {

    public UserEventDto map(UserEventEntity entity, MessageService messageService) {
        return UserEventDto.builder()
            .id(entity.getId())
            .eventType(entity.getEventType())
            .eventTypeName(messageService.getMessage(entity.getEventType()))
            .ipAddress(entity.getIpAddress())
            .clientId(entity.getClientId())
            .browser(entity.getBrowser())
            .device(entity.getDevice())
            .os(entity.getOs())
            .creationDate(entity.getCreationDate())
            .build();
    }
}
