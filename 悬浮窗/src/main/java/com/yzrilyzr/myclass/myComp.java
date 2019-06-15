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
	@Override
	public Comparator reversed()
	{
		// TODO: Implement this method
		return null;
	}

	@Override
	public Comparator thenComparing(Comparator other)
	{
		// TODO: Implement this method
		return null;
	}

	@Override
	public Comparator thenComparing(Function keyExtractor, Comparator keyComparator)
	{
		// TODO: Implement this method
		return null;
	}

	@Override
	public Comparator thenComparing(Function keyExtractor)
	{
		// TODO: Implement this method
		return null;
	}

	@Override
	public Comparator thenComparingInt(ToIntFunction keyExtractor)
	{
		// TODO: Implement this method
		return null;
	}

	@Override
	public Comparator thenComparingLong(ToLongFunction keyExtractor)
	{
		// TODO: Implement this method
		return null;
	}

	@Override
	public Comparator thenComparingDouble(ToDoubleFunction keyExtractor)
	{
		// TODO: Implement this method
		return null;
	}

	@Override
	public static <T extends Comparable<? super T>> Comparator<T> reverseOrder()
	{
		// TODO: Implement this method
		return null;
	}

	@Override
	public static <T extends Comparable<? super T>> Comparator<T> naturalOrder()
	{
		// TODO: Implement this method
		return null;
	}

	@Override
	public static <T extends Object> Comparator<T> nullsFirst(Comparator<? super T> comparator)
	{
		// TODO: Implement this method
		return null;
	}

	@Override
	public static <T extends Object> Comparator<T> nullsLast(Comparator<? super T> comparator)
	{
		// TODO: Implement this method
		return null;
	}

	@Override
	public static<T extends Object, U extends Object> Comparator<T> comparing(Function<? super T, ? extends U> keyExtractor, Comparator<? super U> keyComparator)
	{
		// TODO: Implement this method
		return null;
	}

	@Override
	public static <T extends Object, U extends Comparable<? super U>> Comparator<T> comparing(Function<? super T, ? extends U> keyExtractor)
	{
		// TODO: Implement this method
		return null;
	}

	@Override
	public static <T extends Object> Comparator<T> comparingInt(ToIntFunction<? super T> keyExtractor)
	{
		// TODO: Implement this method
		return null;
	}

	@Override
	public static <T extends Object> Comparator<T> comparingLong(ToLongFunction<? super T> keyExtractor)
	{
		// TODO: Implement this method
		return null;
	}

	@Override
	public static<T extends Object> Comparator<T> comparingDouble(ToDoubleFunction<? super T> keyExtractor)
	{
		// TODO: Implement this method
		return null;
	}
	
	
}
