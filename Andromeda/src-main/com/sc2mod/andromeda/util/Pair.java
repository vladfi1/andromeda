package com.sc2mod.andromeda.util;

/**
 * An ordinary 2-tuple
 * @author gex
 *
 * @param <TYPE_1>
 * @param <TYPE_2>
 */
public final class Pair<TYPE_1, TYPE_2>
{
    public final TYPE_1 _1;
    public final TYPE_2 _2;
 
 
 
    public Pair(final TYPE_1 _1, final TYPE_2 _2)
    {
        this._1 = _1;
        this._2 = _2;
    }
 
    public final boolean equals(Object o)
    {
        if (!(o instanceof Pair<?,?>)) return false;
 
        Pair<?,?> other = (Pair<?,?>) o;
        if (_1 != null && !_1.equals(other._1)) {
            return false;
        } else if (_1 == null && other._1 != null) {
            return false;
        } else if (_2 != null && !_2.equals(other._2)) {
            return false;
        } else if (_2 == null && other._2 != null) {
            return false;
        } else {
            return true;
        }
    }
 
    public int hashCode()
    {
        int hLeft = _1 == null ? 0 : _1.hashCode();
        int hRight = _2 == null ? 0 : _2.hashCode();
 
        return hLeft + (57 * hRight);
    }
    
    public static <T1,T2> Pair<T1, T2> from(final T1 __1, final T2 __2){
    	return new Pair<T1,T2>(__1,__2);
    }
    
    public String toString(){
    	return new StringBuilder()	
    		.append("(")
			.append(String.valueOf(_1))
			.append(",")
			.append(String.valueOf(_2))
			.append(")")
			.toString();
    }
}
