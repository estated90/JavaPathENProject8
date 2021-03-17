package tourguide.helper;

/**
 * @author Nico
 *         <p>
 *         Class dedicated to the testing of the application
 *         </p>
 *
 */
public class InternalTestHelper {

	// Set this default up to 100,000 for testing
	private static int internalUserNumber = 100;

	private InternalTestHelper() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * @param internalUserNumber Number of user needed at the lauch of the application
	 */
	public static void setInternalUserNumber(int internalUserNumber) {
		InternalTestHelper.internalUserNumber = internalUserNumber;
	}


	/**
	 * @return Return the number of user in the session of the application
	 */
	public static int getInternalUserNumber() {
		return internalUserNumber;
	}
}
