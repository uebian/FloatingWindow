package com.yzrilyzr.myclass;
import java.util.Comparator;
import java.util.function.Function;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;
import java.util.function.ToDoubleFunction;
public abstract class myComp<T extends Object> implements Comparator
{

	@Override
	public abstract int compare(T p1, T p2);
	
}
