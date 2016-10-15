package ch.elcathon.fastfood.chzoll.resources;

import java.util.Optional;
import java.util.stream.Collectors;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.CharEncoding;

import ch.elcathon.fastfood.chzoll.api.Basket;
import ch.elcathon.fastfood.chzoll.api.Category;
import ch.elcathon.fastfood.chzoll.api.Item;
import ch.elcathon.fastfood.chzoll.core.Rules;
import ch.elcathon.fastfood.chzoll.db.ItemDao;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Contact;
import io.swagger.annotations.ExternalDocs;
import io.swagger.annotations.Info;
import io.swagger.annotations.License;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;

@SwaggerDefinition(
        info = @Info(
                description = "Calculates customs duties on a basket of items",
                version = "V0.1-SNAPSHOT",
                title = "The CH Zoll API",
                termsOfService = "TBD",
                contact = @Contact(
                   name = "Fast Food @ ELCAthon 2016", 
                   email = "i.tell@you.no", 
                   url = "http://done-have-one.yet"
                ),
                license = @License(
                   name = "MIT (2016) (ELCAthon -- Fast Food Team)", 
                   url = "https://opensource.org/licenses/MIT"
                )
        ),
        consumes = {MediaType.APPLICATION_JSON + ";charset=" + CharEncoding.UTF_8},
        produces = {MediaType.APPLICATION_JSON + ";charset=" + CharEncoding.UTF_8},
        schemes = {SwaggerDefinition.Scheme.HTTP},
        tags = {
                @Tag(name = "CH Zoll", description = "Tag used to denote public CH Zoll API operations")
        }, 
        externalDocs = @ExternalDocs(value = "ch.ch", url = "https://www.ch.ch/en/swiss-customs/")
)
@Api(produces = MediaType.APPLICATION_JSON + ";charset=" + CharEncoding.UTF_8, consumes = MediaType.APPLICATION_JSON
		+ ";charset=" + CharEncoding.UTF_8, protocols = "rest", value = "CH Zoll")
@Path("api/chzoll")
@Consumes(MediaType.APPLICATION_JSON + ";charset=" + CharEncoding.UTF_8)
@Produces(MediaType.APPLICATION_JSON + ";charset=" + CharEncoding.UTF_8)
public class ChZoll {

	@ApiOperation(value = "Finds Items by name", notes = "Currently only a handful of items are statically configured, and everything is lower-case", response = Item.class)
	@ApiResponses({ @ApiResponse(code = 200, message = "Item found"),
			@ApiResponse(code = 204, message = "Item not found") })
	@GET
	@Path("items/{item}")
	public Response map(
			@ApiParam(value = "Name of the item to be found", required = true) @PathParam("item") final String item) {
		Optional<Item> findByName = new ItemDao().findByName(item);
		if (findByName.isPresent()) {
			return Response.ok(findByName.get()).build();
		} else {
			return Response.noContent().build();
		}
	}

	@ApiOperation(value = "Calculates customs duties on items", notes = "Currently only a handful of items are statically configured, and everything is lower-case; the rules should be however more-or-less complete (for importing to Switzerland). Please note that the Category of the Item is re-calculated by the server (we don't trust your category definitions, sorry)", response = Basket.class)
	@ApiResponses({ @ApiResponse(code = 200, message = "Basket with customs duties created") })
	@POST
	@Path("check")
	public Response check(
			@ApiParam(value = "Where are you importing from?", required = true) @QueryParam("from") final String from,
			@ApiParam(value = "Where are you importing to?", required = true) @QueryParam("to") final String to,
			@ApiParam(value = "How many of you are doing the import?", required = true) @QueryParam("importers") final int importers,
			@ApiParam(value = "What is that you want to import?") final Basket productBasket) {
		return Response.ok(Rules.processBasket(from, to, importers, fillBasket(productBasket))).build();
	}

	private Basket fillBasket(final Basket toFill) {
		return new Basket(toFill.getItems().stream()
				.map(i -> new Item(i.getName(), i.getAmount(),
						i.getComment(), new ItemDao().findByName(i.getName())
								.orElse(new Item("", 0d, "", new Category(""))).getCategory()))
				.collect(Collectors.toList()));
	}
}
