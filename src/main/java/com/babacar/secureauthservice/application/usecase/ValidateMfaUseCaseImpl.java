package com.babacar.secureauthservice.application.usecase;


import com.babacar.secureauthservice.domain.port.in.ValidateMfaUseCase;
import com.babacar.secureauthservice.domain.service.MfaService;
import org.springframework.stereotype.Service;

@Service
public class ValidateMfaUseCaseImpl implements ValidateMfaUseCase {

    private final MfaService mfaService;

    public ValidateMfaUseCaseImpl(MfaService mfaService) {
        this.mfaService = mfaService;
    }

    @Override
    public boolean validate(String email, String code) {
        return mfaService.verify(null, code);
    }
}
