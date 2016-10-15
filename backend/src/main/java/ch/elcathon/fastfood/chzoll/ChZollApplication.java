package ch.elcathon.fastfood.chzoll;

import java.util.EnumSet;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;

import org.eclipse.jetty.servlets.CrossOriginFilter;

import ch.elcathon.fastfood.chzoll.health.ChZollHealthChecker;
import ch.elcathon.fastfood.chzoll.resources.ChZoll;
import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.federecio.dropwizard.swagger.SwaggerBundle;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;

public class ChZollApplication extends Application<ChZollConfiguration> {
	public static void main(String[] args) throws Exception {
		new ChZollApplication().run(args);
	}

	@Override
	public String getName() {
		return "CH Zoll Application";
	}

	@Override
	public void initialize(Bootstrap<ChZollConfiguration> bootstrap) {
		// Enable variable substitution with environment variables
		bootstrap.setConfigurationSourceProvider(new SubstitutingSourceProvider(
				bootstrap.getConfigurationSourceProvider(), new EnvironmentVariableSubstitutor(false)));

		bootstrap.addBundle(new AssetsBundle());
		bootstrap.addBundle(new SwaggerBundle<ChZollConfiguration>() {
			@Override
			protected SwaggerBundleConfiguration getSwaggerBundleConfiguration(ChZollConfiguration configuration) {
				return configuration.swaggerBundleConfiguration;
			}
		});
	}

	@Override
	public void run(ChZollConfiguration configuration, Environment environment) {
		// Enable CORS headers
	    final FilterRegistration.Dynamic cors =
	        environment.servlets().addFilter("CORS", CrossOriginFilter.class);

	    // Configure CORS parameters
	    cors.setInitParameter("allowedOrigins", "*");
	    cors.setInitParameter("allowedHeaders", "X-Requested-With,Content-Type,Accept,Origin");
	    cors.setInitParameter("allowedMethods", "OPTIONS,GET,PUT,POST,DELETE,HEAD");

	    // Add URL mapping
	    cors.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");
		
		environment.healthChecks().register("alive", new ChZollHealthChecker());
		environment.jersey().register(new ChZoll());
	}
}
