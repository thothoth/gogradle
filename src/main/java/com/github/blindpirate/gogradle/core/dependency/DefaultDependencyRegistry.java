package com.github.blindpirate.gogradle.core.dependency;

import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;

@Singleton
public class DefaultDependencyRegistry implements DependencyRegistry {
    private Map<String, ResolvedDependency> packages = new HashMap<>();

    @Override
    public boolean register(ResolvedDependency resolvedDependency) {
        synchronized (packages) {
            ResolvedDependency existent = packages.get(resolvedDependency.getName());
            if (existent != null && theyAreAllFirstLevel(existent, resolvedDependency)) {
                throw new IllegalStateException("First-level package " + resolvedDependency.getName()
                        + " conflict!");
            } else if (resolvedDependency.isFirstLevel()
                    || existingModuleIsOutOfDate(existent, resolvedDependency)) {
                packages.put(resolvedDependency.getName(), resolvedDependency);
                return true;
            } else {
                return false;
            }
        }
    }

    @Override
    public ResolvedDependency retrive(String name) {
        return packages.get(name);
    }

    private boolean existingModuleIsOutOfDate(ResolvedDependency existingModule,
                                              ResolvedDependency resolvedDependency) {
        if (existingModule == null) {
            return true;
        }
        return existingModule.getUpdateTime() < resolvedDependency.getUpdateTime();
    }

    private boolean theyAreAllFirstLevel(ResolvedDependency existedModule, ResolvedDependency resolvedDependency) {
        return existedModule.isFirstLevel() && resolvedDependency.isFirstLevel();
    }
}
