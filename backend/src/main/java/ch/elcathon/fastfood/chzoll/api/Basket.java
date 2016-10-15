package ch.elcathon.fastfood.chzoll.api;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.concurrent.Immutable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "Basket", description = "A list of Items what you have")
@Immutable
public class Basket {

	@ApiModelProperty(value = "The list of Items in the Basket", required = true)
	@JsonDeserialize(as = LinkedList.class)
	private final List<Item> items;

	@JsonCreator
	public Basket(@JsonProperty("items") final List<Item> items) {
		this.items = items;
	}

	public List<Item> getItems() {
		return Collections.unmodifiableList(items);
	}

	@Override
	public String toString() {
		return "Basket [items=" + items + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((items == null) ? 0 : items.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Basket other = (Basket) obj;
		if (items == null) {
			if (other.items != null)
				return false;
		} else if (!items.equals(other.items))
			return false;
		return true;
	}
}
