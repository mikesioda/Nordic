package com.nordic.thread;

/**
 * Defines the interface used in NotifyingThread.
 * 
 * @author Mike Sioda
 *
 */
public interface ThreadCompleteInterface {
    void notifyOfThreadComplete(final Thread thread);
}
