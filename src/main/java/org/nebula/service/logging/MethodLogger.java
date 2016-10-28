/*
 * Copyright 2002-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.nebula.service.logging;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * Logs method calls.
 *
 * <p>It is an AspectJ aspect and you are not supposed to use it directly. It is instantiated by
 * AspectJ runtime framework when your code is annotated with {@link Loggable} annotation.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @checkstyle IllegalThrows (500 lines)
 * @since 0.7.2
 */
@Aspect
public final class MethodLogger {

  /**
   * Currently running methods.
   */
  private final transient Set<Marker> running =
      new ConcurrentSkipListSet<Marker>();

  /**
   * Public ctor.
   */
  public MethodLogger() {
//        final ScheduledExecutorService monitor =
//            Executors.newSingleThreadScheduledExecutor(
//                new NamedThreads(
//                    "loggable",
//                    "watching of @Loggable annotated methods"
//                )
//            );
//        monitor.scheduleWithFixedDelay(
//            new VerboseRunnable(
//                new Runnable() {
//
//                    public void run() {
//                        for (final MethodLogger.Marker marker
//                            : MethodLogger.this.running) {
//                            marker.monitor();
//                        }
//                    }
//                }
//            ),
//            1, 1, TimeUnit.SECONDS
//        );
  }

  /**
   * Log one line.
   *
   * @param level   Level of logging
   * @param log     Destination log
   * @param message Message to log
   */
  private static void log(final int level, final Class<?> log,
                          final String message) {
    if (level == Loggable.TRACE) {
      logger(log).trace(message);
//            Logger.trace(log, message);
    } else if (level == Loggable.DEBUG) {
      logger(log).debug(message);
//            Logger.debug(log, message);
    } else if (level == Loggable.INFO) {
      logger(log).info(message);
//            Logger.info(log, message);
    } else if (level == Loggable.WARN) {
      logger(log).warn(message);
//            Logger.warn(log, message);
    } else if (level == Loggable.ERROR) {
      logger(log).error(message);
//            Logger.error(log, message);
    }
  }

  /**
   * Log level is enabled?
   *
   * @param level Level of logging
   * @param log   Destination log
   * @return TRUE if enabled
   */
  private static boolean enabled(final int level, final Class<?> log) {
    boolean enabled = true;
//        if (level == Loggable.TRACE) {
//            enabled = Logger.isTraceEnabled(log);
//        } else if (level == Loggable.DEBUG) {
//            enabled = Logger.isDebugEnabled(log);
//        } else if (level == Loggable.INFO) {
//            enabled = Logger.isInfoEnabled(log);
//        } else if (level == Loggable.WARN) {
//            enabled = Logger.isWarnEnabled(log);
//        } else {
//            enabled = true;
//        }
    return enabled;
  }

  /**
   * Checks whether array of types contains given type.
   *
   * @param array Array of them
   * @param exp   The exception to find
   * @return TRUE if it's there
   */
  private static boolean contains(final Class<? extends Throwable>[] array,
                                  final Throwable exp) {
    boolean contains = false;
    for (final Class<? extends Throwable> type : array) {
      if (MethodLogger.instanceOf(exp.getClass(), type)) {
        contains = true;
        break;
      }
    }
    return contains;
  }

  /**
   * The type is an instance of another type?
   *
   * @param child  The child type
   * @param parent Parent type
   * @return TRUE if child is really a child of a parent
   */
  private static boolean instanceOf(final Class<?> child,
                                    final Class<?> parent) {
    boolean instance = child.equals(parent)
                       || (child.getSuperclass() != null
                           && MethodLogger.instanceOf(child.getSuperclass(), parent));
    if (!instance) {
      for (final Class<?> iface : child.getInterfaces()) {
        instance = MethodLogger.instanceOf(iface, parent);
        if (instance) {
          break;
        }
      }
    }
    return instance;
  }

  /**
   * Textualize a stacktrace.
   *
   * @param trace Array of stacktrace elements
   * @return The text
   */
  private static String textualize(final StackTraceElement[] trace) {
    final StringBuilder text = new StringBuilder();
    for (int pos = 0; pos < trace.length; ++pos) {
      if (text.length() > 0) {
        text.append(", ");
      }
      text.append(
          String.format(
              "%s#%s[%d]",
              trace[pos].getClassName(),
              trace[pos].getMethodName(),
              trace[pos].getLineNumber()
          )
      );
    }
    return text.toString();
  }

  /**
   * Get the instance of the logger for this particular caller.
   *
   * @param source Source of the logging operation
   * @return The instance of {@code Logger} class
   */
  private static org.slf4j.Logger logger(final Object source) {
    final org.slf4j.Logger logger;
    if (source instanceof Class) {
      logger = LoggerFactory.getLogger((Class<?>) source);
    } else {
      logger = LoggerFactory.getLogger(source.getClass());
    }
    return logger;
  }

  /**
   * Log methods in a class.
   *
   * <p>Try NOT to change the signature of this method, in order to keep it backward compatible.
   *
   * @param point Joint point
   * @return The result of call
   * @throws Throwable If something goes wrong inside
   */
  @Around(
      // @checkstyle StringLiteralsConcatenation (7 lines)
      "execution(public * (@org.nebula.service.logging.Loggable *).*(..))"
      + " && !execution(String *.toString())"
      + " && !execution(int *.hashCode())"
      + " && !execution(boolean *.canEqual(Object))"
      + " && !execution(boolean *.equals(Object))"
      + " && !cflow(call(org.nebula.service.logging.MethodLogger.new()))"
  )
  public Object wrapClass(final ProceedingJoinPoint point) throws Throwable {
    final Method method =
        MethodSignature.class.cast(point.getSignature()).getMethod();
    Object output;
    if (method.isAnnotationPresent(Loggable.class)) {
      output = point.proceed();
    } else {
      output = this.wrap(
          point,
          method,
          method.getDeclaringClass().getAnnotation(Loggable.class)
      );
    }
    return output;
  }

  /**
   * Log individual methods.
   *
   * <p>Try NOT to change the signature of this method, in order to keep it backward compatible.
   *
   * @param point Joint point
   * @return The result of call
   * @throws Throwable If something goes wrong inside
   */
  @Around(
      // @checkstyle StringLiteralsConcatenation (2 lines)
      "(execution(* *(..)) || initialization(*.new(..)))"
      + " && @annotation(org.nebula.service.logging.Loggable)"
  )
  @SuppressWarnings("PMD.AvoidCatchingThrowable")
  public Object wrapMethod(final ProceedingJoinPoint point) throws Throwable {
    final Method method =
        MethodSignature.class.cast(point.getSignature()).getMethod();
    return this.wrap(point, method, method.getAnnotation(Loggable.class));
  }

  /**
   * Catch exception and re-call the method.
   *
   * @param point      Joint point
   * @param method     The method
   * @param annotation The annotation
   * @return The result of call
   * @throws Throwable If something goes wrong inside
   * @checkstyle ExecutableStatementCount (50 lines)
   */
  private Object wrap(final ProceedingJoinPoint point, final Method method,
                      final Loggable annotation) throws Throwable {
    if (Thread.interrupted()) {
      throw new IllegalStateException(
          String.format(
              "thread '%s' in group '%s' interrupted",
              Thread.currentThread().getName(),
              Thread.currentThread().getThreadGroup().getName()
          )
      );
    }
    final long start = System.nanoTime();
    final MethodLogger.Marker marker =
        new MethodLogger.Marker(point, annotation);
    this.running.add(marker);
    try {
      final Class<?> type = method.getDeclaringClass();
      int level = annotation.value();
      final int limit = annotation.limit();
      if (annotation.prepend()) {
        MethodLogger.log(
            level,
            type,
            new StringBuilder(
                Mnemos.toText(
                    point,
                    annotation.trim(),
                    annotation.skipArgs()
                )
            ).append(": entered").toString()
        );
      }
      final Object result = point.proceed();
      final long nano = System.nanoTime() - start;
      final boolean over = nano > annotation.unit().toNanos(limit);
      if (MethodLogger.enabled(level, type) || over) {
        final StringBuilder msg = new StringBuilder();
        msg.append(
            Mnemos.toText(
                point,
                annotation.trim(),
                annotation.skipArgs()
            )
        ).append(':');
        if (!method.getReturnType().equals(Void.TYPE)) {
          msg.append(' ').append(
              Mnemos.toText(
                  result,
                  annotation.trim(),
                  annotation.skipResult()
              )
          );
        }
        msg.append(String.format(" in %d s", nano));
        if (over) {
          level = Loggable.WARN;
          msg.append(" (too slow!)");
        }
        MethodLogger.log(
            level,
            type,
            msg.toString()
        );
      }
      return result;
      // @checkstyle IllegalCatch (1 line)
    } catch (final Throwable ex) {
      if (!MethodLogger.contains(annotation.ignore(), ex)
          && !ex.getClass().isAnnotationPresent(Loggable.Quiet.class)) {
        final StackTraceElement trace = ex.getStackTrace()[0];
        MethodLogger.log(
            Loggable.ERROR,
            method.getDeclaringClass(),
            String.format(
                "%s: thrown %s out of %s#%s[%d] in %d s",
                Mnemos.toText(
                    point,
                    annotation.trim(),
                    annotation.skipArgs()
                ),
                Mnemos.toText(ex),
                trace.getClassName(),
                trace.getMethodName(),
                trace.getLineNumber(),
                System.nanoTime() - start
            )
        );
      }
      throw ex;
    } finally {
      this.running.remove(marker);
    }
  }

  /**
   * Marker of a running method.
   */
  private static final class Marker
      implements Comparable<Marker> {

    /**
     * When the method was started, in milliseconds.
     */
    private final transient long started = System.currentTimeMillis();
    /**
     * Which monitoring cycle was logged recently.
     */
    private final transient AtomicInteger logged = new AtomicInteger();
    /**
     * The thread it's running in.
     */
    @SuppressWarnings("PMD.DoNotUseThreads")
    private final transient Thread thread = Thread.currentThread();
    /**
     * Joint point.
     */
    private final transient ProceedingJoinPoint point;
    /**
     * Annotation.
     */
    private final transient Loggable annotation;

    /**
     * Public ctor.
     *
     * @param pnt  Joint point
     * @param annt Annotation
     */
    protected Marker(final ProceedingJoinPoint pnt, final Loggable annt) {
      this.point = pnt;
      this.annotation = annt;
    }

    /**
     * Monitor it's status and log the problem, if any.
     */
    public void monitor() {
      final TimeUnit unit = this.annotation.unit();
      final long threshold = this.annotation.limit();
      final long age = unit.convert(
          System.currentTimeMillis() - this.started, TimeUnit.MILLISECONDS
      );
      final int cycle = (int) ((age - threshold) / threshold);
      if (cycle > this.logged.get()) {
        final Method method = MethodSignature.class.cast(
            this.point.getSignature()
        ).getMethod();
        logger(method.getDeclaringClass())
            .warn(" %s: takes more than %[ms]s, %[ms]s already, thread=%s/%s",
                  Mnemos.toText(this.point, true, this.annotation.skipArgs()));
//                Logger.warn(
//                    method.getDeclaringClass(),
//                    "%s: takes more than %[ms]s, %[ms]s already, thread=%s/%s",
//                    Mnemos.toText(this.point, true, this.annotation.skipArgs()),
//                    TimeUnit.MILLISECONDS.convert(threshold, unit),
//                    TimeUnit.MILLISECONDS.convert(age, unit),
//                    this.thread.getName(),
//                    this.thread.getState()
//                );
//                Logger.debug(
//                    method.getDeclaringClass(),
//                    "%s: thread %s/%s stacktrace: %s",
//                    Mnemos.toText(this.point, true, this.annotation.skipArgs()),
//                    this.thread.getName(),
//                    this.thread.getState(),
//                    MethodLogger.textualize(this.thread.getStackTrace())
//                );
        this.logged.set(cycle);
      }
    }

    @Override
    public int hashCode() {
      return this.point.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
      return obj == this || MethodLogger.Marker.class.cast(obj)
          .point.equals(this.point);
    }

    public int compareTo(final Marker marker) {
      int cmp = 0;
      if (this.started < marker.started) {
        cmp = 1;
      } else if (this.started > marker.started) {
        cmp = -1;
      }
      return cmp;
    }
  }


}