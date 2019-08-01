package ru.javaops.masterjava.util;

import lombok.experimental.UtilityClass;

/**
 * @see <a href="https://github.com/diffplug/durian/blob/master/src/com/diffplug/common/base/Errors.java">full Errors at Durian project</a>
 */
@UtilityClass
public class Exceptions {
    public static <E extends Exception> Runnable wrap(Functions.Specific.Runnable<E> runnableWitEx) {
        return () -> {
            try {
                runnableWitEx.run();
            } catch (Exception e) {
                throw asRuntime(e);
            }
        };
    }

    public static RuntimeException asRuntime(Throwable t) {
        if (t instanceof RuntimeException) {
            return (RuntimeException) t;
        } else {
            return new RuntimeException(t);
        }
    }
}
