package com.mfino.util;

public class Pair<K,V> {
	K firstElement;
	V secondElement;
	
	public Pair(K first, V second){
		this.firstElement = first;
		this.secondElement = second;
	}
	
	public K getFirst(){
		return firstElement;
	}
	
	public V getSecond(){
		return secondElement; 
	}

}
