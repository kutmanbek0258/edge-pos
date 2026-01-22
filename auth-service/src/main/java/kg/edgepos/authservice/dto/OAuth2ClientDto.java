package kg.edgepos.authservice.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.validation.constraints.NotNull;
import kg.edgepos.authservice.dto.converters.AuthMethodJsonSerializer;
import kg.edgepos.authservice.dto.converters.GrantTypeJsonDeserializer;
import kg.edgepos.authservice.dto.converters.GrantTypeJsonSerializer;
import kg.edgepos.authservice.dto.converters.AuthMethodJsonDeserializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OAuth2ClientDto {

    @NotNull
    private String clientId;
    private String clientSecret;
    private LocalDate clientSecretExpiresAt;
    private String clientName;

    @JsonSerialize(contentUsing = AuthMethodJsonSerializer.class)
    @JsonDeserialize(contentUsing = AuthMethodJsonDeserializer.class)
    private Set<ClientAuthenticationMethod> clientAuthenticationMethods;

    @JsonSerialize(contentUsing = GrantTypeJsonSerializer.class)
    @JsonDeserialize(contentUsing = GrantTypeJsonDeserializer.class)
    private Set<AuthorizationGrantType> authorizationGrantTypes;
    private Set<String> redirectUris;
    private Set<String> scopes;
    private Set<String> deleteNotifyUris;
}
