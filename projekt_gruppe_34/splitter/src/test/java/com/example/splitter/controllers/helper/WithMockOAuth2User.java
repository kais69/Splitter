package com.example.splitter.controllers.helper;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.example.splitter.controllers.helper.WithOAuth2UserSecurityContextFactory;
import org.springframework.security.test.context.support.WithSecurityContext;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithOAuth2UserSecurityContextFactory.class)
public @interface WithMockOAuth2User {
    int id() default 666666;


    String login() default "username";

    String[] roles() default {"USER"};

    String[] authorities() default {};


    String clientRegistrationId() default "github";
}
