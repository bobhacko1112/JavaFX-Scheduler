/*
Binary wrapper for <T>. Unwraps only if
enumerated to Some(T).
 */


package option;

import unwrap.Unwrap;



/**
 *
 * @author bob
 */
public class Option<T> {
	private final T t;
	private final Opt o;

	private enum Opt {
		SOME, NONE;
	}

	private Option(T t, Opt o) {
		this.t = t;
		this.o = o;
	}

	public Option() {
		this.t = null;
		this.o = Opt.NONE;
	}

	public static <T> Option Some(T t) {
		return new Option(t, Opt.SOME);
	}


	public static Option None() {
		return new Option(Opt.NONE, Opt.NONE);
	}

	public void Unwrap(Unwrap f) {
		if (this.o == Opt.SOME) {
			f.Unwrap(this.t);
		}
	}

}





