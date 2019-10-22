package ru.javaops.masterjava.persist;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

@Slf4j
public class TimingExtension implements BeforeTestExecutionCallback, AfterTestExecutionCallback {
    @Override
    public void beforeTestExecution(ExtensionContext context) {
        log.info("\n\n+++ Start " + context.getDisplayName());
    }

    @Override
    public void afterTestExecution(ExtensionContext context) {
        log.info("\n+++ Finish " + context.getDisplayName() + '\n');
    }
}
