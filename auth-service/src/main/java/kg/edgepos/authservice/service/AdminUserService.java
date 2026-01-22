package kg.edgepos.authservice.service;

import kg.edgepos.authservice.dto.AdminUserDto;
import kg.edgepos.authservice.dto.PageableResponseDto;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

public interface AdminUserService {

    PageableResponseDto<AdminUserDto> searchUsers(int page, int pageSize, String email);

    void assignAdmin(String email);

    void dismissAdmin(UUID userId);

    ResponseEntity<byte[]> getAvatar(UUID avatarFileId);
}
