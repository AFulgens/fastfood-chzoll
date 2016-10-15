package ch.elcathon.fastfood.chzoll.health;

import com.codahale.metrics.health.HealthCheck;

public class ChZollHealthChecker extends HealthCheck {
	@Override
	protected Result check() throws Exception {
		return Result.healthy(); // we are still up and running
	}
}
