package ch.elcathon.fastfood.chzoll.core;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import ch.elcathon.fastfood.chzoll.api.Basket;
import ch.elcathon.fastfood.chzoll.api.Category;
import ch.elcathon.fastfood.chzoll.api.Item;

public class Rules {

	public static Basket processBasket(final String from, final String to, final int importers,
			final Basket inputBasket) {
		if (!"Switzerland".equals(to)) {
			throw new WebApplicationException(
					Response.status(Status.BAD_REQUEST).entity("Only customs into Switzerland supported yet").build());
		}
		final List<Item> ret = new LinkedList<>();

		Basket remainder = inputBasket;
		remainder = applyMeatsRule(remainder, importers, ret);
		remainder = applyDiariesRule(remainder, importers, ret);
		remainder = applyOilsRule(remainder, importers, ret);
		remainder = applyNonSpiritsRule(remainder, importers, ret);
		remainder = applySpiritsRule(remainder, importers, ret);
		remainder = applyCigarettesRule(remainder, importers, ret);
		remainder = applyTobaccoRule(remainder, importers, ret);
		remainder = applyWeaponsRule(remainder, importers, ret);
		applySkipRule(remainder, ret);

		return new Basket(ret);
	}

	private static Basket applyMeatsRule(final Basket from, final int importers, final List<Item> to) {
		return applyTwoStepRule(from, importers, to, "meat", 1d, 17d, 10d, 23d);
	}

	private static Basket applyDiariesRule(final Basket from, final int importers, final List<Item> to) {
		return applyOneStepRule(from, importers, to, "diary", 1d, 16d);
	}

	private static Basket applyOilsRule(final Basket from, final int importers, final List<Item> to) {
		return applyOneStepRule(from, importers, to, "oil", 5d, 2d);
	}

	private static Basket applyNonSpiritsRule(final Basket from, final int importers, final List<Item> to) {
		return applyOneStepRule(from, importers, to, "nonspirit", 5d, 2d);
	}

	private static Basket applySpiritsRule(final Basket from, final int importers, final List<Item> to) {
		return applyOneStepRule(from, importers, to, "spirit", 1d, 15d);
	}

	private static Basket applyCigarettesRule(final Basket from, final int importers, final List<Item> to) {
		return applyOneStepRule(from, importers, to, "cigarettes", 250d, 0.25d);
	}

	private static Basket applyTobaccoRule(final Basket from, final int importers, final List<Item> to) {
		return applyOneStepRule(from, importers, to, "tobacco", 250d, 0.1d);
	}

	private static Basket applyWeaponsRule(final Basket from, final int importers, final List<Item> to) {
		from.getItems().stream().filter(i -> i.getCategory().equals(new Category("weapon")))
				.forEach(i -> to.add(new Item(i.getName(), i.getAmount(), WEAPONS_TEXT, i.getCategory())));
		return new Basket(from.getItems().stream().filter(i -> !i.getCategory().equals(new Category("weapon")))
				.collect(Collectors.toList()));
	}

	private static void applySkipRule(final Basket from, final List<Item> to) {
		from.getItems().stream().forEach(i -> to.add(i));
	}

	private static Basket applyOneStepRule(final Basket from, final int importers, final List<Item> to,
			final String what, final double limit, final double price) {
		double amountOf = sumUp(from, what);
		double amount = 0d;
		if (Double.compare(amountOf, importers * limit) >= 0) {
			amount = (amountOf - importers * limit) * price;
		}
		double amountToPay = amount;
		from.getItems().stream().filter(i -> i.getCategory().equals(new Category(what)))
				.forEach(i -> to.add(new Item(i.getName(), i.getAmount(),
						"PAY:" + amountToPay / amountOf * i.getAmount(), i.getCategory())));
		return filterOut(from, what);
	}

	private static Basket applyTwoStepRule(final Basket from, final int importers, final List<Item> to,
			final String what, final double limit, final double price, final double limit2, final double price2) {
		double amountOf = sumUp(from, what);
		double amount = 0d;
		if (Double.compare(amountOf, importers * limit) >= 0) {
			if (Double.compare(amountOf, importers * limit2) >= 0) {
				amount = (importers * limit - 1) * price;
				amount = (amountOf - importers * limit2) * price2;
			} else {
				amount = (amountOf - importers * limit) * price;
			}
		}
		double amountToPay = amount;
		if (Double.compare(amount, 0d) != 0) {
			from.getItems().stream().filter(i -> i.getCategory().equals(new Category(what)))
					.forEach(i -> to.add(new Item(i.getName(), i.getAmount(),
							"PAY:" + amountToPay / amountOf * i.getAmount(), i.getCategory())));
		}
		return filterOut(from, what);
	}

	private static double sumUp(final Basket from, final String what) {
		return from.getItems().stream().filter(i -> i.getCategory().equals(new Category(what)))
				.mapToDouble(i -> i.getAmount()).sum();
	}

	private static Basket filterOut(final Basket from, final String what) {
		return new Basket(from.getItems().stream().filter(i -> !i.getCategory().equals(new Category(what)))
				.collect(Collectors.toList()));
	}

	private Rules() {
		// prevent instantiation of utility class
	}

	private static final String WEAPONS_TEXT = "Weapons\n\nObligation to notify at the border\nAll weapons, parts of weapons, ammunition and parts of ammunition must be declared to the customs office on importation, exportation and transit.";
}
