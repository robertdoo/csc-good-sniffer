package Filter;


public class GeneralHashFunctionLibrary {

	public long RSHash(String str, int prime) {
		int b = 378551;
		int a = 63689;
		long hash = 0;

		for (int i = 0; i < str.length(); i++) {
			hash = hash * a + str.charAt(i);
			a = a * b;
		}

		// return Math.abs(hash);
		return Math.abs(hash) % prime;
	}

	/* End Of RS Hash Function */

	public long JSHash(String str, int prime) {
		long hash = 1315423911;

		for (int i = 0; i < str.length(); i++) {
			hash ^= ((hash << 5) + str.charAt(i) + (hash >> 2));
		}

		// return hash;
		return Math.abs(hash) % prime;
	}

	/* End Of JS Hash Function */

	public long PJWHash(String str, int prime) {
		long BitsInUnsignedInt = (long) (4 * 8);
		long ThreeQuarters = (long) ((BitsInUnsignedInt * 3) / 4);
		long OneEighth = (long) (BitsInUnsignedInt / 8);
		long HighBits = (long) (0xFFFFFFFF) << (BitsInUnsignedInt - OneEighth);
		long hash = 0;
		long test = 0;

		for (int i = 0; i < str.length(); i++) {
			hash = (hash << OneEighth) + str.charAt(i);

			if ((test = hash & HighBits) != 0) {
				hash = ((hash ^ (test >> ThreeQuarters)) & (~HighBits));
			}
		}

		// return hash;
		return Math.abs(hash) % prime;
	}

	/* End Of P. J. Weinberger Hash Function */

	public long ELFHash(String str, int prime) {
		long hash = 0;
		long x = 0;

		for (int i = 0; i < str.length(); i++) {
			hash = (hash << 4) + str.charAt(i);

			if ((x = hash & 0xF0000000L) != 0) {
				hash ^= (x >> 24);
			}
			hash &= ~x;
		}

		// return hash;
		return Math.abs(hash) % prime;
	}

	/* End Of ELF Hash Function */

	public long BKDRHash(String str, int prime) {
		long seed = 131; // 31 131 1313 13131 131313 etc..
		long hash = 0;

		for (int i = 0; i < str.length(); i++) {
			hash = (hash * seed) + str.charAt(i);
		}

		// return hash;
		return Math.abs(hash) % prime;
	}

	/* End Of BKDR Hash Function */

	public long SDBMHash(String str, int prime) {
		long hash = 0;

		for (int i = 0; i < str.length(); i++) {
			hash = str.charAt(i) + (hash << 6) + (hash << 16) - hash;
		}

		// return hash;
		return Math.abs(hash) % prime;
	}

	/* End Of SDBM Hash Function */

	public long DJBHash(String str, int prime) {
		long hash = 5381;

		for (int i = 0; i < str.length(); i++) {
			hash = ((hash << 5) + hash) + str.charAt(i);
		}

		// return hash;
		return Math.abs(hash) % prime;
	}

	/* End Of DJB Hash Function */

	public long DEKHash(String str, int prime) {
		long hash = str.length();

		for (int i = 0; i < str.length(); i++) {
			hash = ((hash << 5) ^ (hash >> 27)) ^ str.charAt(i);
		}

		// return hash;
		return Math.abs(hash) % prime;
	}

	/* End Of DEK Hash Function */

	public long BPHash(String str, int prime) {
		long hash = 0;

		for (int i = 0; i < str.length(); i++) {
			hash = hash << 7 ^ str.charAt(i);
		}

		// return hash;
		return Math.abs(hash) % prime;
	}

	/* End Of BP Hash Function */

	public long FNVHash(String str, int prime) {
		long fnv_prime = 0x811C9DC5;
		long hash = 0;

		for (int i = 0; i < str.length(); i++) {
			hash *= fnv_prime;
			hash ^= str.charAt(i);
		}

		// return hash;
		return Math.abs(hash) % prime;
	}

	/* End Of FNV Hash Function */

	public long APHash(String str, int prime) {
		long hash = 0xAAAAAAAA;

		for (int i = 0; i < str.length(); i++) {
			if ((i & 1) == 0) {
				hash ^= ((hash << 7) ^ str.charAt(i) * (hash >> 3));
			} else {
				hash ^= (~((hash << 11) + str.charAt(i) ^ (hash >> 5)));
			}
		}

		// return hash;
		return Math.abs(hash) % prime;
	}
	/* End Of AP Hash Function */

}

