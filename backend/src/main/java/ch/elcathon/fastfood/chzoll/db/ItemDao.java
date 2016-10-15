package ch.elcathon.fastfood.chzoll.db;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

import ch.elcathon.fastfood.chzoll.api.Category;
import ch.elcathon.fastfood.chzoll.api.Item;

public class ItemDao {

	private static final Map<String, Item> ITEMS = new TreeMap<>();
	static {
		// in-memory static db
		ITEMS.put("beef", new Item("beef", 0d, "", new Category("meat")));
		ITEMS.put("pork", new Item("beef", 0d, "", new Category("meat")));
		ITEMS.put("butter", new Item("butter", 0d, "", new Category("diary")));
		ITEMS.put("cream", new Item("butter", 0d, "", new Category("diary")));
		ITEMS.put("olive oil", new Item("olive oil", 0d, "", new Category("oil")));
		ITEMS.put("fat", new Item("fat", 0d, "", new Category("oil")));
		ITEMS.put("beer", new Item("beer", 0d, "", new Category("nonspirit")));
		ITEMS.put("wine", new Item("wine", 0d, "", new Category("nonspirit")));
		ITEMS.put("vodka", new Item("vodka", 0d, "", new Category("spirit")));
		ITEMS.put("rum", new Item("rum", 0d, "", new Category("spirit")));
		ITEMS.put("cigarette (box)", new Item("cigarette (box)", 0d, "", new Category("cigarettes")));
		ITEMS.put("snus", new Item("snus", 0d, "", new Category("tobacco")));
		ITEMS.put("crossbow", new Item("crossbow", 0d, "", new Category("weapon")));
		ITEMS.put("cake", new Item("cake", 0d, "", new Category("food")));
	}
	
	public ItemDao() {
		// nop
	}
	
	public Optional<Item> findByName(String name) {
		return Optional.ofNullable(ITEMS.get(name));
	}
	
	public Item create(Item item) {
		return persist(item);
	}
	
	public List<Item> findAll() {
		return ITEMS.values().stream().collect(Collectors.toList());
	}
	
	private static Item persist(Item item) {
		return ITEMS.put(item.getName(), item);
	}
}
