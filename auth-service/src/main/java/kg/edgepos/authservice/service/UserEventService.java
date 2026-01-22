package kg.edgepos.authservice.service;

import jakarta.servlet.http.HttpServletRequest;
import kg.edgepos.authservice.dao.type.UserEventType;
import kg.edgepos.authservice.dto.PageableResponseDto;
import kg.edgepos.authservice.dto.UserEventDto;

public interface UserEventService {

    PageableResponseDto<UserEventDto> searchEvents(int page, int pageSize);

    void createEvent(UserEventType eventType, String clientId, HttpServletRequest request);

    /**
     * Удалить события пользователя, являющиеся устаревшими.
     */
    void deleteOldEvents();
}
