package com.github.phantauth.test

import groovy.transform.CompileStatic
import org.spockframework.runtime.extension.IGlobalExtension
import org.spockframework.runtime.model.SpecInfo

@CompileStatic
class TestExtension implements IGlobalExtension {

    @Override
    void start() {
        TestComponent.Holder.init()
        TestComponent.Holder.instance.server().start()
    }

    @Override
    void visitSpec(SpecInfo spec) {
    }

    @Override
    void stop() {
    }
}
