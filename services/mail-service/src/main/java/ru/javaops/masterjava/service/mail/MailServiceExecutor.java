package ru.javaops.masterjava.service.mail;

import lombok.extern.slf4j.Slf4j;
import one.util.streamex.StreamEx;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;

@Slf4j
public class MailServiceExecutor {
    private static final String OK = "OK";

    private static final String INTERRUPTED_BY_FAULTS_NUMBER = "+++ Interrupted by faults number";
    private static final String INTERRUPTED_BY_TIMEOUT = "+++ Interrupted by timeout";
    private static final String INTERRUPTED_EXCEPTION = "+++ InterruptedException";

    private static final ExecutorService mailExecutor = Executors.newFixedThreadPool(8);

    public static GroupResult sendBulk(final Set<Addressee> addressees, final String subject, final String body) throws Exception {
        final CompletionService<MailResult> competitionService = new ExecutorCompletionService<>(mailExecutor);
        List<Future<MailResult>> futures = StreamEx.of(addressees)
                .map(addressee -> competitionService.submit(() -> MailSender.sendTo(addressee, subject, body)))
                .toList();
        return new Callable<GroupResult>() {
            private int success = 0;
            private List<MailResult> failed = new ArrayList<>();

            @Override
            public GroupResult call() {
                while (!futures.isEmpty()) {
                    try {
                        Future<MailResult> future = competitionService.poll(10, TimeUnit.SECONDS);
                        if (future == null) {
                            return cancelWithFail(INTERRUPTED_BY_TIMEOUT);
                        }
                        futures.remove(future);
                        MailResult result = future.get();
                        if (result.isOk()) {
                            success++;
                        } else {
                            failed.add(result);
                            if (failed.size() >= 5) {
                                return cancelWithFail(INTERRUPTED_BY_FAULTS_NUMBER);
                            }
                        }
                    } catch (ExecutionException e) {
                        return cancelWithFail(e.getCause().toString());
                    } catch (InterruptedException e) {
                        return cancelWithFail(INTERRUPTED_EXCEPTION);
                    }
                }
                var groupResult = new GroupResult(success, failed, null);
                log.info("groupResult: {}", groupResult);
                return groupResult;
            }

            private GroupResult cancelWithFail(String cause) {
                futures.forEach(f -> f.cancel(true));
                return new GroupResult(success, failed, cause);
            }
        }.call();
    }
}