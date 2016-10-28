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

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;


@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface Loggable {

  /**
   * TRACE level of logging.
   */
  int TRACE = 0;

  /**
   * DEBUG level of logging.
   */
  int DEBUG = 1;

  /**
   * INFO level of logging.
   */
  int INFO = 2;

  /**
   * WARN level of logging.
   */
  int WARN = 3;

  /**
   * ERROR level of logging.
   */
  int ERROR = 4;

  /**
   * Level of logging.
   */
  int value() default Loggable.INFO;

  /**
   * Maximum amount allowed for this method (a warning will be issued if it takes longer).
   *
   * @since 0.7.6
   */
  int limit() default 1;

  /**
   * Time unit for the limit.
   *
   * @since 0.7.14
   */
  TimeUnit unit() default TimeUnit.MINUTES;

  /**
   * Shall we trim long texts in order to make log lines more readable?
   *
   * @since 0.7.13
   */
  boolean trim() default true;

  /**
   * Method entry moment should be reported as well (by default only an exit moment is reported).
   *
   * @since 0.7.16
   */
  boolean prepend() default false;

  /**
   * List of exception types, which should not be logged if thrown.
   *
   * <p>You can also mark some exception types as "always to be ignored", using {@link
   * Loggable.Quiet} annotation.
   *
   * @since 0.7.17
   */
  Class<? extends Throwable>[] ignore() default {};

  /**
   * Skip logging of result, replacing it with dots?
   *
   * @since 0.7.19
   */
  boolean skipResult() default false;

  /**
   * Skip logging of arguments, replacing them all with dots?
   *
   * @since 0.7.19
   */
  boolean skipArgs() default false;

  /**
   * Identifies an exception that is never logged by {@link Loggable} if/when being thrown out of an
   * annotated method.
   *
   * <p>Sometimes exceptions are used as flow control instruments (although this may be considered
   * as a bad practice in most casts). In such situations we don't want to flood log console with
   * error messages. One of the options is to use {@link Loggable#ignore()} attribute to list all
   * exception types that should be ignored. However, this {@link Loggable.Quiet} annotation is more
   * convenient when we want to ignore one specific exception type in all situations.
   *
   * @since 0.8
   */
  @Documented
  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.TYPE)
  public @interface Quiet {

  }

}


