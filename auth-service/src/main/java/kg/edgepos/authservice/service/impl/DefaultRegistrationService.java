package kg.edgepos.authservice.service.impl;

import com.google.common.collect.ImmutableMap;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kg.edgepos.authservice.components.OTPStore;
import kg.edgepos.authservice.components.RegistrationStore;
import kg.edgepos.authservice.exception.InformationException;
import kg.edgepos.authservice.exception.RegistrationException;
import kg.edgepos.authservice.service.MessageService;
import kg.edgepos.authservice.dto.RegistrationDto;
import kg.edgepos.authservice.service.MailSenderService;
import kg.edgepos.authservice.service.RegistrationService;
import kg.edgepos.authservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DefaultRegistrationService implements RegistrationService {

    private final UserService userService;
    private final OTPStore otpStore;
    private final RegistrationStore registrationStore;
    private final MessageService messageService;
    private final MailSenderService mailSenderService;

    @Override
    public void register(RegistrationDto registrationDto, HttpServletResponse response) {
        // проверяем что пользователь с таким email ещё не существует
        if (userService.existByEmail(registrationDto.getEmail())) {
            throw InformationException.builder("$account.already.exist").build();
        }

        // Создаём OTP
        OTPStore.GenerationResult generationResult = otpStore.generate(response);

        // Сохраняем данные во временное хранилище
        try {
            registrationStore.save(registrationDto, generationResult.sessionId());
        } catch (Exception e) {
            throw InformationException.builder("$happened.unexpected.error").build();
        }

//         отправляем OTP по email
        mailSenderService.sendNewMail(
            registrationDto.getEmail(),
            messageService.getMessage("email.subject.confirm.registration"),
            ImmutableMap.<String, Object>builder()
                .put("firstName", registrationDto.getFirstName())
                .put("otp", generationResult.otp())
                .build()
                .toString()
        );
    }

    @Override
    @Transactional
    public void checkOtp(String otp, HttpServletRequest request) {
        if (!otpStore.validate(otp, request)) {
            throw new RegistrationException("$opt.incorrect");
        }

        String sessionId = otpStore.getSessionId(request);
        RegistrationDto registrationDto;
        try {
            registrationDto = registrationStore.take(sessionId);
        } catch (Exception e) {
            throw InformationException.builder("$happened.unexpected.error").build();
        }
        userService.saveAndActivateUser(registrationDto);
    }
}
