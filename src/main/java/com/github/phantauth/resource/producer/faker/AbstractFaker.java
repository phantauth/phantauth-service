package com.github.phantauth.resource.producer.faker;

import com.devskiller.jfairy.Fairy;
import com.github.javafaker.Faker;
import com.github.phantauth.resource.Producer;

abstract class AbstractFaker<T> implements Producer<T> {

    static final Faker faker = new Faker();
    static final Fairy fairy = Fairy.builder().build();
}

