package kg.edgepos.authservice.service.impl;

import kg.edgepos.authservice.dao.entity.UserEntity;
import kg.edgepos.authservice.dao.repository.UserRepository;
import kg.edgepos.authservice.mapper.AuthorizedUserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity entity = userRepository.findByEmail(username);
        if (entity == null) {
            throw new UsernameNotFoundException("User with username = " + username + " not found");
        }
        return AuthorizedUserMapper.map(entity, null);
    }
}
