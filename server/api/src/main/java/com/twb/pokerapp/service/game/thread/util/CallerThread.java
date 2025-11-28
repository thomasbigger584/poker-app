package com.twb.pokerapp.service.game.thread.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Denotes that the annotated method is intended to be executed on the caller's thread,
 * as opposed to being dispatched to an internal worker thread (e.g., a GameThread).
 * This serves as a documentation and static analysis hint.
 */
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.METHOD, ElementType.CONSTRUCTOR})
public @interface CallerThread {
}