
package jdz.bukkitUtils.misc;

import lombok.Data;

@Data
public class Pair<K extends Object, V extends Object> {
	private final K key;
	private final V value;
}
