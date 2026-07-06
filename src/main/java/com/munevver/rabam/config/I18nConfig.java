package com.munevver.rabam.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import java.util.List;
import java.util.Locale;

@Configuration
public class I18nConfig {

    @Bean
    public LocaleResolver localeResolver() {
        AcceptHeaderLocaleResolver localeResolver = new AcceptHeaderLocaleResolver();

        localeResolver.setDefaultLocale(Locale.forLanguageTag("tr"));
        localeResolver.setSupportedLocales(List.of(
                Locale.forLanguageTag("tr"),
                Locale.forLanguageTag("en")
        ));

        return localeResolver;
    }

    @Bean
    public MessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();

        messageSource.setBasename("messages");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setDefaultLocale(Locale.forLanguageTag("tr"));
        messageSource.setFallbackToSystemLocale(false);

        return messageSource;
    }
}