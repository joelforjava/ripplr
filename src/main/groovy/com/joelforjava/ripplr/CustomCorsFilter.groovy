package com.joelforjava.ripplr

import groovy.transform.CompileStatic
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@CompileStatic
class CustomCorsFilter extends org.springframework.web.filter.CorsFilter {

    CustomCorsFilter() {
        super(configurationSource())
    }

    private static UrlBasedCorsConfigurationSource configurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true)
        config.addAllowedOrigin('http://localhost:4200')
        config.addAllowedOrigin('http://localhost:4201')
        ['origin', 'authorization', 'accept', 'content-type', 'x-requested-with'].each { header ->
            config.addAllowedHeader(header)
        }
        ['GET', 'HEAD', 'POST', 'PUT', 'DELETE', 'OPTIONS'].each { method ->
            config.addAllowedMethod(method)
        }
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration('/**', config)
        return source
    }

}
