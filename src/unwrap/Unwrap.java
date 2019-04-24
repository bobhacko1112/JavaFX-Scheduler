/*
An interface to allow passing of lamdas; unpacking the contained object.
 */
package unwrap;

/**
 *
 * @author bob
 */
public interface Unwrap<C> {
	public void Unwrap(Object f);
}