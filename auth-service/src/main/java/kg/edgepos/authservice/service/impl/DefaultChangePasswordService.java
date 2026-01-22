package kg.edgepos.authservice.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kg.edgepos.authservice.components.ConfirmationStore;
import kg.edgepos.authservice.components.OTPStore;
import kg.edgepos.authservice.exception.ChangePasswordException;
import kg.edgepos.authservice.exception.InformationException;
import kg.edgepos.authservice.service.MessageService;
import kg.edgepos.authservice.dto.security.AuthorizedUser;
import kg.edgepos.authservice.service.ChangePasswordService;
import kg.edgepos.authservice.service.UserService;
import kg.edgepos.authservice.utils.SecurityUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class DefaultChangePasswordService implements ChangePasswordService {

    private final static String PASSWORD_KEY = "password";

    private final OTPStore otpStore;
    private final ConfirmationStore changePasswordStore;
    private final MessageService messageService;
    private final UserService userService;


    public DefaultChangePasswordService(OTPStore otpStore,
                                        @Qualifier("changePasswordStore")ConfirmationStore changePasswordStore,
                                        MessageService messageService,
                                        UserService userService) {
        this.otpStore = otpStore;
        this.changePasswordStore = changePasswordStore;
        this.messageService = messageService;
        this.userService = userService;
    }

    @Override
    public void init(String newPassword, HttpServletResponse response) {
        AuthorizedUser authorizedUser = SecurityUtils.getAuthUser();
        OTPStore.GenerationResult generationResult = otpStore.generate(response);
        try {
            ConfirmationStore.StoreItem storeItem = new ConfirmationStore.StoreItem(
                authorizedUser.getEmail(),
                generationResult.otp(),
                Map.of(PASSWORD_KEY, newPassword)
            );
            changePasswordStore.save(storeItem, generationResult.sessionId());
        } catch (Exception e) {
            throw InformationException.builder("$happened.unexpected.error").build();
        }

//        mailSenderService.sendNewMail(
//            authorizedUser.getEmail(),
//            messageService.getMessage("email.subject.init.reset.password"),
//            ImmutableMap.<String, Object>builder()
//                .put("firstName", authorizedUser.getFirstName())
//                .put("otp", generationResult.otp())
//                .build()
//                .toString()
//        );
    }

    @Override
    public void confirmChange(String otp, HttpServletRequest request) {
        otp = otp.trim();
        if (!otpStore.validate(otp, request)) {
            throw new ChangePasswordException("$opt.incorrect");
        }

        String sessionId = otpStore.getSessionId(request);
        ConfirmationStore.StoreItem storeItem;
        try {
            storeItem = changePasswordStore.take(sessionId);
        } catch (Exception e) {
            throw InformationException.builder("$happened.unexpected.error").build();
        }

        Map<String, String> extraData = storeItem.extraData();
        if (extraData == null
            || !extraData.containsKey(PASSWORD_KEY)
            || StringUtils.isEmpty(extraData.get(PASSWORD_KEY))) {
            throw new ChangePasswordException("$data.not.found");
        }

        userService.changePassword(storeItem.email(), extraData.get(PASSWORD_KEY));
    }
}
