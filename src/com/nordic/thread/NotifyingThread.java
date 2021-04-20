package com.nordic.thread;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * NotifyingThread is used to implement an thread notificaiton interface.  When
 * actions executed via the applicaiton GUI are complete
 * notifyListeners() is called.
 * 
 * @author Mike Sioda
 *
 */
public abstract class NotifyingThread extends Thread {
	  private final Set<ThreadCompleteInterface> listeners
	                   = new CopyOnWriteArraySet<ThreadCompleteInterface>();
	  public final void addListener(final ThreadCompleteInterface listener) {
	    listeners.add(listener);
	  }
	  public final void removeListener(final ThreadCompleteInterface listener) {
	    listeners.remove(listener);
	  }
	  private final void notifyListeners() {
	    for (ThreadCompleteInterface listener : listeners) {
	      listener.notifyOfThreadComplete(this);
	    }
	  }
	  @Override
	  public final void run() {
	    try {
	      doRun();
	    } finally {
	      notifyListeners();
	    }
	  }
	  public abstract void doRun();
	}