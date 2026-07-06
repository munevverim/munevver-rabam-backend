package com.munevver.rabam.common.i18n;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class I18nMessageService {

    private final MessageSource messageSource;

    public String getMessage(String code, Object... args) {
        return messageSource.getMessage(
                code,
                args,
                LocaleContextHolder.getLocale()
        );
    }
}