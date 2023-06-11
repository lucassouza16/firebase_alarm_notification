package com.lucassouza.firebase.alarm.firebase_alarm_notification.util;

import java.util.ArrayList;
import java.util.Collection;

public class QueryList<T> extends ArrayList<T> {
	private static final long serialVersionUID = 1L;

	public QueryList () {}

	public QueryList (Collection collection) {
		super(collection);
	}
	@FunctionalInterface
	public interface ForEachCallback1<T> {
		void call(T argument, Integer index);
	}
	@FunctionalInterface
	public interface ForEachCallback2<T> {
		void call(T argument);
	}
	@FunctionalInterface
	public interface FindCallback1<T> {
		boolean call(T argument, Integer index);
	}
	@FunctionalInterface
	public interface FindCallback2<T> {
		boolean call(T argument);
	}
	public void foreach(ForEachCallback1<T> call) {
		for (int i = 0; i < this.size(); i++) {
			call.call(this.get(i), i);
		}
	}
	public void foreach(ForEachCallback2<T> call) {
		for (int i = 0; i < this.size(); i++) {
			call.call(this.get(i));
		}
	}
	public T find(FindCallback1<T> call) {
		for (int i = 0; i < this.size(); i++) {
			T element = this.get(i);

			if (call.call(element, i))
				return element;
		}

		return null;
	}
	public T find(FindCallback2<T> call) {
		for (int i = 0; i < this.size(); i++) {
			T element = this.get(i);

			if (call.call(element))
				return element;
		}

		return null;
	}

	public T first() {
		if(this.isEmpty()){
			return null;
		}

		return this.get(0);
	}
}
