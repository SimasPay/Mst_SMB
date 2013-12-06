package com.mfino.util;

import java.util.concurrent.atomic.AtomicInteger;

public class UniqueNumberGen 
{
	static AtomicInteger num = new AtomicInteger();
	public static int getNextNum()
	{
		return num.addAndGet(1);
	}
}
