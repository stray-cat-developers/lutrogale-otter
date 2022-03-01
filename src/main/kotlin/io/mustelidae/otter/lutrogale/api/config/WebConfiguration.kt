package io.mustelidae.otter.lutrogale.api.config

import com.fasterxml.jackson.databind.DeserializationFeature
import io.mustelidae.smoothcoatedotter.utils.Jackson
import io.mustelidae.smoothcoatedotter.web.commons.filter.CrossScriptingFilter
import io.mustelidae.smoothcoatedotter.web.commons.filter.UrlBaseLoginFilter
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.format.datetime.standard.DateTimeFormatterRegistrar
import org.springframework.format.support.FormattingConversionService
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.converter.StringHttpMessageConverter
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.servlet.config.annotation.DelegatingWebMvcConfiguration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer
import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver
import java.time.format.DateTimeFormatter


@Configuration
@ControllerAdvice
class WebConfiguration(
    private val osoriLoginHandlerInterceptor: OsoriLoginHandlerInterceptor
) : DelegatingWebMvcConfiguration() {

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(osoriLoginHandlerInterceptor)
        super.addInterceptors(registry)
    }

    override fun configureMessageConverters(converters: MutableList<HttpMessageConverter<*>>) {
        val objectMapper = Jackson.getMapper()
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        converters.add(StringHttpMessageConverter())
        converters.add(MappingJackson2HttpMessageConverter(objectMapper))
        super.configureMessageConverters(converters)
    }

    @Bean
    fun requestResponseLogFilter(): FilterRegistrationBean<RequestResponseLogFilter> {
        return FilterRegistrationBean<RequestResponseLogFilter>().apply {
            filter = RequestResponseLogFilter()
            order = 1
        }
    }

    @Bean
    fun crossScriptingFilter(): FilterRegistrationBean<*> {
        return FilterRegistrationBean<CrossScriptingFilter>().apply {
            filter = CrossScriptingFilter()
            order = 2
        }
    }

    @Bean
    fun loginFilter(): FilterRegistrationBean<*> {
        return FilterRegistrationBean<UrlBaseLoginFilter>().apply {
            filter = UrlBaseLoginFilter()
            order = 3
        }
    }

    override fun addViewControllers(registry: ViewControllerRegistry) {
        registry.addViewController("/swagger-ui/")
            .setViewName("forward:/swagger-ui/index.html")

        registry.addViewController("/view/management/members").setViewName("management/member/members")
        registry.addViewController("/view/management/new-member").setViewName("management/member/new-member")
        registry.addViewController("/view/management/new-member/{userId}/authority-grant")
            .setViewName("management/member/new-member-authority")
        registry.addViewController("/view/management/new-member/{userId}/personal-grant")
            .setViewName("management/member/new-member-personal")
        registry.addViewController("/view/management/new-member/{userId}/complete")
            .setViewName("management/member/complete")
        registry.addViewController("/view/profile").setViewName("profile")
        registry.addViewController("/view/new-project").setViewName("project/new/input-project")
        registry.addViewController("/view/new-project/{projectId}/navi")
            .setViewName("project/new/input-navigation-tree")
        registry.addViewController("/view/new-project/{projectId}/auth-groups")
            .setViewName("project/new/input-authority-groups")
        registry.addViewController("/view/new-project/{projectId}/complete").setViewName("project/new/complete")
        registry.addViewController("/view/project/{projectId}/configuration/navigation")
            .setViewName("project/configuration/navigation")
        registry.addViewController("/view/project/{projectId}/configuration/authority")
            .setViewName("project/configuration/authority-groups")
        registry.addViewController("/view/project/{projectId}/configuration/members")
            .setViewName("project/configuration/members")

        super.addViewControllers(registry)
    }

    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/")
        registry.addResourceHandler("/**").addResourceLocations("classpath:/public/")
        registry.addResourceHandler("/templates/**").addResourceLocations("classpath:/templates/")
        super.addResourceHandlers(registry)
    }

    @Bean
    override fun mvcConversionService(): FormattingConversionService {
        val conversionService = super.mvcConversionService()
        val dateTimeRegistrar = DateTimeFormatterRegistrar()
        dateTimeRegistrar.setDateFormatter(DateTimeFormatter.ISO_DATE)
        dateTimeRegistrar.setTimeFormatter(DateTimeFormatter.ISO_TIME)
        dateTimeRegistrar.setDateTimeFormatter(DateTimeFormatter.ISO_DATE_TIME)
        dateTimeRegistrar.registerFormatters(conversionService)
        return conversionService
    }

    @Bean
    fun freemarkerViewResolver(): FreeMarkerViewResolver {
        val resolver = FreeMarkerViewResolver()
        resolver.isCache = false
        resolver.setPrefix("")
        resolver.setSuffix(".ftl")
        resolver.setContentType("text/html;charset=utf-8")
        return resolver
    }

    @Bean
    fun freemarkerConfig(): FreeMarkerConfigurer {
        val freeMarkerConfigurer = FreeMarkerConfigurer()
        freeMarkerConfigurer.setTemplateLoaderPath("classpath:/templates/")
        return freeMarkerConfigurer
    }
}
