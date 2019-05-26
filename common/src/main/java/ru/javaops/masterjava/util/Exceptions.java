package ru.javaops.masterjava.util;

import lombok.experimental.UtilityClass;

/**
 * @see <a href="https://github.com/diffplug/durian/blob/master/src/com/diffplug/common/base/Errors.java">full Errors at Durian project</a>
 */
@UtilityClass
public class Exceptions {
    public static <E extends Exception> java.lang.Runnable wrap(Functions.Specific.Runnable<E> runnableWithEx) {
        return () -> {
            try {
                runnableWithEx.run();
            } catch (Exception e) {
                throw asRuntime(e);
            }
        };
    }

    public static RuntimeException asRuntime(Throwable e) {
        return e instanceof RuntimeException ? (RuntimeException) e : new RuntimeException(e);
    }
}
