package kg.edgepos.authservice.service;

import kg.edgepos.authservice.dto.UserTokenInfoDto;

import java.util.List;

public interface UserTokenService {

    List<UserTokenInfoDto> getUserTokens();

    void recallToken(String authenticationId);

    void recallAllCurrentUserTokens();
}
