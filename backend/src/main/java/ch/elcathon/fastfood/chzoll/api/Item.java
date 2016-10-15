package ch.elcathon.fastfood.chzoll.api;

import javax.annotation.concurrent.Immutable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "Item", description = "A (yet crude) definition of an item")
@Immutable
public class Item {
	@ApiModelProperty(value = "Name of the Item (considered to be a unique key)", required = true)
	@JsonProperty("name")
	private final String name;
	@ApiModelProperty(value = "Amount of the item (makes sense only within a basket)", required = false)
	@JsonProperty("amount")
	private final double amount;
	@ApiModelProperty(value = "Comment on the item", required = false)
	@JsonProperty("comment")
	private final String comment;
	@ApiModelProperty(value = "Category of the item", required = false)
	@JsonProperty("category")
	private final Category category;

	@JsonCreator
	public Item(@JsonProperty("name") String name, @JsonProperty("amount") double amount,
			@JsonProperty(value = "comment", required = false) String comment,
			@JsonProperty(value = "category", required = false) Category category) {
		this.name = name;
		this.amount = amount;
		this.comment = comment;
		this.category = category;
	}

	public String getName() {
		return name;
	}

	public double getAmount() {
		return amount;
	}

	public String getComment() {
		return comment;
	}

	public Category getCategory() {
		return category;
	}

	@Override
	public String toString() {
		return "Item [name=" + name + ", amount=" + amount + ", comment=" + comment + ", category=" + category + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(amount);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((category == null) ? 0 : category.hashCode());
		result = prime * result + ((comment == null) ? 0 : comment.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		Item other = (Item) obj;
		if (Double.doubleToLongBits(amount) != Double.doubleToLongBits(other.amount))
			return false;
		if (category == null) {
			if (other.category != null)
				return false;
		} else if (!category.equals(other.category))
			return false;
		if (comment == null) {
			if (other.comment != null)
				return false;
		} else if (!comment.equals(other.comment))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
}