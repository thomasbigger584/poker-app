package com.twb.pokerapp.data.auth;

import android.util.Log;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A {@link MutableLiveData} that delivers each emission at most once and never re-delivers the last
 * value to a newly-attached or re-attached observer.
 *
 * <p>Plain {@code LiveData} is sticky: it caches the latest value and replays it whenever a new
 * observer starts (configuration change, navigating to another screen that observes the same
 * event). For a one-shot signal like "log out now" that replay would fire the action again — e.g.
 * a second {@code BaseAuthActivity} would immediately sign out even though the logout was already
 * handled. The pending flag here guarantees a single consumption.
 *
 * <p>A logout triggered while no screen is observing (app backgrounded, websocket service detects a
 * dead token) is still honoured: the next observer to become active consumes it once.
 */
public class SingleLiveEvent<T> extends MutableLiveData<T> {
    private static final String TAG = SingleLiveEvent.class.getSimpleName();
    private final AtomicBoolean pending = new AtomicBoolean(false);

    @MainThread
    @Override
    public void observe(@NonNull LifecycleOwner owner, @NonNull final Observer<? super T> observer) {
        if (hasActiveObservers()) {
            Log.w(TAG, "Multiple observers registered but only one will be notified of changes.");
        }
        super.observe(owner, t -> {
            if (pending.compareAndSet(true, false)) {
                observer.onChanged(t);
            }
        });
    }

    @MainThread
    @Override
    public void setValue(@Nullable T t) {
        pending.set(true);
        super.setValue(t);
    }
}
