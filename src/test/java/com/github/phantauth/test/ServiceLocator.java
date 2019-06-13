package com.github.phantauth.test;

import com.github.phantauth.core.Tenant;
import lombok.experimental.Delegate;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

public class ServiceLocator extends TestHelper implements TestRule, TestComponent {

    @Delegate
    private final TestComponent component;

    public ServiceLocator() {
        super(TestComponent.Holder.instance);
        this.component = TestComponent.Holder.instance;
    }

    @Override
    public Statement apply(final Statement base, final Description description) {
        return base;
    }

    public Tenant getTenant() {
        return component.getTenantRepository().getDefaultTenant();
    }

    public TestHelper getAt(Tenant tenant) {
        return new TestHelper(component, tenant);
    }
}
