package nighttimedriver.chat;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MapUtil {
	public static <K, V> boolean zip(Iterable<K> keys, Iterable<V> values, Map<? super K, ? super V> dst) {
		Iterator<K> keyIter = keys.iterator();
		Iterator<V> valIter = values.iterator();
		while (keyIter.hasNext() && valIter.hasNext()) {
		    dst.put(keyIter.next(), valIter.next());
		}
		return (keyIter.hasNext() || valIter.hasNext());
	}
	
	public static <K, V> Map<K, V> fromArrays(K[] keys, V[] values) {
		Map<K, V> map = new HashMap<K, V>();
		int length = Math.min(keys.length, values.length);
		for (int i = 0; i < length; i++) {
			map.put(keys[i], values[i]);
		}
		return map;
	}
}
