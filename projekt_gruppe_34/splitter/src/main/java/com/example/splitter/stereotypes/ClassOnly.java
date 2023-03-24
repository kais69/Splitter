package com.example.splitter.stereotypes;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.CONSTRUCTOR})

@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface ClassOnly {
}
