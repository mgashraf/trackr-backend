package de.techdev.trackr.core.web.api;

import de.techdev.trackr.core.web.converters.DateConverter;
import de.techdev.trackr.domain.ApiBeansConfiguration;
import de.techdev.trackr.domain.common.TrackrUserLocaleResolver;
import de.techdev.trackr.domain.company.Address;
import de.techdev.trackr.domain.company.Company;
import de.techdev.trackr.domain.company.ContactPerson;
import de.techdev.trackr.domain.employee.Employee;
import de.techdev.trackr.domain.employee.login.Authority;
import de.techdev.trackr.domain.employee.login.Credential;
import de.techdev.trackr.domain.employee.vacation.VacationRequest;
import de.techdev.trackr.domain.project.BillableTime;
import de.techdev.trackr.domain.project.Project;
import de.techdev.trackr.domain.project.WorkTime;
import org.springframework.context.annotation.*;
import org.springframework.core.convert.support.ConfigurableConversionService;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestMvcConfiguration;
import org.springframework.format.FormatterRegistry;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.List;

/**
 * @author Moritz Schulze
 */
@Configuration
@EnableWebMvc
@ComponentScan(basePackages = "de.techdev.trackr.domain",
        useDefaultFilters = false,
        includeFilters = {
                @ComponentScan.Filter(type = FilterType.ANNOTATION, value = {Controller.class, ControllerAdvice.class, RepositoryEventHandler.class})
        })
@Import({ApiBeansConfiguration.class})
public class ApiWebMvcConfiguration extends RepositoryRestMvcConfiguration {

    @Override
    protected void configureRepositoryRestConfiguration(RepositoryRestConfiguration config) {
        config.exposeIdsFor(new Class[]{Employee.class, Credential.class, Authority.class, Company.class, ContactPerson.class,
                Address.class, Project.class, WorkTime.class, BillableTime.class, VacationRequest.class});
        config.setReturnBodyOnUpdate(true);
        config.setReturnBodyOnCreate(true);
    }

    @Bean
    public LocaleResolver localeResolver() {
        return new TrackrUserLocaleResolver();
    }

    @Bean
    public ExceptionHandlers exceptionHandlers() {
        return new ExceptionHandlers();
    }

    @Bean
    public DateConverter dateConverter() {
        return new DateConverter();
    }

    @Override
    protected void configureConversionService(ConfigurableConversionService conversionService) {
        super.configureConversionService(conversionService);
        conversionService.addConverter(dateConverter());
    }

    /**
     * Somehow the "normal" Spring MVC (not spring-data-rest) does not use the converter registered in {@link #configureConversionService} so we have to register it again.
     */
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(dateConverter());
    }

    @Bean
    public JsonMappingHandlerExceptionResolver jsonMappingHandlerExceptionResolver() {
        return new JsonMappingHandlerExceptionResolver();
    }

    @Override
    public void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> exceptionResolvers) {
        exceptionResolvers.add(jsonMappingHandlerExceptionResolver());
        super.configureHandlerExceptionResolvers(exceptionResolvers);
    }
}