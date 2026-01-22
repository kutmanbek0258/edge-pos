package kg.edgepos.authservice.mapper;

import kg.edgepos.authservice.dao.entity.AuthorityEntity;
import kg.edgepos.authservice.dao.entity.RoleEntity;
import kg.edgepos.authservice.dao.entity.UserEntity;
import kg.edgepos.authservice.dto.security.AuthorizedUser;
import kg.edgepos.authservice.type.AuthProvider;
import lombok.experimental.UtilityClass;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class AuthorizedUserMapper {

    public AuthorizedUser map(UserEntity entity, AuthProvider provider) {
        List<GrantedAuthority> authorities = getUserAuthorities(entity);
        return AuthorizedUser.builder(entity.getEmail(), entity.getPasswordHash(), authorities)
            .id(entity.getId())
            .firstName(entity.getFirstName())
            .lastName(entity.getLastName())
            .middleName(entity.getMiddleName())
            .birthday(entity.getBirthday())
            .avatarFileId(entity.getAvatarFileId())
            .registrationDate(entity.getCreationDate().toLocalDate())
            .admin(entity.getAdmin())
            .superuser(entity.getSuperuser())
            .build();
    }

    public AuthorizedUser reload(AuthorizedUser authorizedUser, UserEntity entity) {
        authorizedUser.setLastName(entity.getLastName());
        authorizedUser.setFirstName(entity.getFirstName());
        authorizedUser.setMiddleName(entity.getMiddleName());
        authorizedUser.setBirthday(entity.getBirthday());
        authorizedUser.setRegistrationDate(entity.getCreationDate().toLocalDate());
        authorizedUser.setAvatarFileId(entity.getAvatarFileId());
        authorizedUser.setAdmin(entity.getAdmin());
        authorizedUser.setSuperuser(entity.getSuperuser());
        return authorizedUser;
    }

    public List<GrantedAuthority> getUserAuthorities(UserEntity entity) {
        return entity.getRoles().stream()
            .filter(RoleEntity::getActive)
            .flatMap(role -> role.getAuthorities().stream())
            .filter(AuthorityEntity::getActive)
            .map(authority -> new SimpleGrantedAuthority(authority.getCode()))
            .collect(Collectors.toList());
    }
}
