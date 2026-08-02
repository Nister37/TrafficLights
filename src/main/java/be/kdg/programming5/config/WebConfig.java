package be.kdg.programming5.config;

import be.kdg.programming5.presentation.converter.StringToTrafficLightStatusConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import java.util.Arrays;
import java.util.Locale;

/**
 * Web configuration class to set up automatic language detection and custom converters.
 * Language is automatically detected from browser's Accept-Language header.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final StringToTrafficLightStatusConverter trafficLightStatusConverter;
    public WebConfig(StringToTrafficLightStatusConverter trafficLightStatusConverter) {
        this.trafficLightStatusConverter = trafficLightStatusConverter;
    }

    @Bean
    public LocaleResolver localeResolver() {
        AcceptHeaderLocaleResolver resolver = new AcceptHeaderLocaleResolver();
        // Set supported locales: English and Polish
        resolver.setSupportedLocales(Arrays.asList(Locale.ENGLISH, new Locale("pl")));
        // Set default locale to English if browser language is not supported
        resolver.setDefaultLocale(Locale.ENGLISH);
        return resolver;
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(trafficLightStatusConverter);
    }
}
