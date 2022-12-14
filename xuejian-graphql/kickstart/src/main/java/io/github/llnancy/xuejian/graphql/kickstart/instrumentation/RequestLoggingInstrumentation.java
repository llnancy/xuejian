package io.github.llnancy.xuejian.graphql.kickstart.instrumentation;

import graphql.ExecutionResult;
import graphql.execution.ExecutionId;
import graphql.execution.instrumentation.InstrumentationContext;
import graphql.execution.instrumentation.SimpleInstrumentation;
import graphql.execution.instrumentation.SimpleInstrumentationContext;
import graphql.execution.instrumentation.parameters.InstrumentationExecutionParameters;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * 请求日志记录 Instrumentation
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/5/19
 */
@Component
@Slf4j
public class RequestLoggingInstrumentation extends SimpleInstrumentation {

    @Override
    public InstrumentationContext<ExecutionResult> beginExecution(InstrumentationExecutionParameters parameters) {
        LocalDateTime startTime = LocalDateTime.now();
        ExecutionId executionId = parameters.getExecutionInput().getExecutionId();
        log.info("{}: query: {} with variables: {}", executionId, parameters.getQuery(), parameters.getVariables());
        return SimpleInstrumentationContext.whenCompleted((executionResult, throwable) -> {
            Duration duration = Duration.between(startTime, LocalDateTime.now());
            if (throwable == null) {
                log.info("{}: completed successfully in: {}", executionId, duration);
            } else {
                log.error("{}: failed in: {}", executionId, duration, throwable);
            }
        });
    }
}
