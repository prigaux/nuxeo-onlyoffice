package fr.edu.lyon.nuxeo.auth;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.test.CoreFeature;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

import com.google.inject.Inject;

import fr.edu.lyon.nuxeo.jwt.service.JWTPayloadPluginService;

@RunWith(FeaturesRunner.class)
@Features(CoreFeature.class)
@Deploy(value = { "fr.edu.lyon.nuxeo.platform.login.jwt"})
public class TestPayloadPluginService
{
	@Inject
	private JWTPayloadPluginService payloadPluginService;

	@Test
	public void testPayloadService()
	{
		assertThat(payloadPluginService, notNullValue());
	}
}
