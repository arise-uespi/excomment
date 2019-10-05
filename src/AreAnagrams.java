public class AreAnagrams {
	public static boolean areAnagrams(String a, String b) {
		boolean value = false;
		for (int i = 0; i < b.length() - 1; i++) {
			value = eatWord(a, b.substring(i, i + 1));
		}
		return value;
	}

	public static boolean eatWord(String a, String b) {
		if (a.contains(b)) {
			return eatWord(a.replace(b, ""), b);
		}
		return a.length() > 0;
	}

	public static boolean areAnagrams2(String a, String b) {
		a = a.trim().toLowerCase();
		b = b.trim().toLowerCase();
		String aux = a;
		for (int j = 0; j < b.length(); j++) {
			String b1 = b.substring(j, j+1);
			if (aux.contains(b1)) {
				aux = aux.replaceFirst(b1, "");
			} else {
				return false;
			}
		}
		return aux.length() == 0;
	}

	public static void main(String[] args) {
		System.out.println(areAnagrams2("orchestra", "carthorse"));
	}
}