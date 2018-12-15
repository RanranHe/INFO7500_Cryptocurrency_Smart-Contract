package hw5;

import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.params.MainNetParams;
import com.google.common.base.CharMatcher;

public class CustomAddressGenerator {
	private static final NetworkParameters Net_Params = MainNetParams.get();
	private static final int MAX_ADDRESS_LENGTH = 35;
	private static long attempts;

	/*
	 * @param prefix string of letters in base58 encoding
	 * 
	 * @returns a Bitcoin address on mainnet which starts with 1 followed prefix.
	 */
	public static String get(String prefix) {
		if (checkValid(prefix)) {
			String address;
			System.out.println("Searching for a bitcoin address that starts with: " + prefix);
			do {
				address = new ECKey().toAddress(Net_Params).toString();
				attempts++;
				if (attempts % 100000 == 0) {
					System.out.println("Thread " + Thread.currentThread().getName()
							+ " is still working, Attempts: " + attempts);
				}
			} while (!(address.startsWith(prefix)));
			System.out.println("Exiting thread " + Thread.currentThread().getName() + ", Attempths made: " + attempts);
			return address;
		} else {
			System.out.println("Your prefix is not a valid bitcoin address substring.");
			return null;
		}
	}

	/**
	 * Verifies that the 1 followed prefix represents a valid bitcoin address
	 * substring
	 *
	 * @param substring the requested phrase
	 * @return true if the requested phrase is a valid bitcoin address substring
	 */
	private static boolean checkValid(final String substring) {
		boolean validity = true;
		if (!CharMatcher.JAVA_LETTER_OR_DIGIT.matchesAllOf(substring) || substring.length() > MAX_ADDRESS_LENGTH
				|| CharMatcher.anyOf("OIl0").matchesAnyOf(substring)) {
			validity = false;
		}
		return validity;
	}

	public static void main(String[] args) {
		String address = get("1HEXX");
		System.out.println("Address: " + address);
	}

}
