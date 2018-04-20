package fr.edu.lyon.nuxeo.test.onlyoffice;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.test.CoreFeature;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

import com.google.inject.Inject;

import fr.edu.lyon.nuxeo.jwt.service.JWTPayloadPluginService;
import fr.edu.lyon.nuxeo.jwt.service.OnlyOfficePayloadResolver;
import fr.edu.lyon.nuxeo.jwt.service.PayloadResolver;

@RunWith(FeaturesRunner.class)
@Features(CoreFeature.class)
@Deploy(value = { "fr.edu.lyon.nuxeo.platform.login.jwt","fr.edu.lyon.nuxeo.onlyoffice"})
public class TestOnlyofficePayloadResolver
{
	@Inject
	private JWTPayloadPluginService payloadPluginService;

	@Test
	public void testPayloadResolver() throws Exception
	{
		Map<String, Object> payloadClaims=new HashMap<>();
		Map<String, Object> payloads=new HashMap<>();
		payloadClaims.put("payload", payloads);
		payloads.put("key", "macle");
		List<Map<String, Object>> actions=new ArrayList<>();
		payloads.put("actions",actions);
		Map<String, Object> action=new HashMap<>();
		action.put("userid", "user");
		actions.add(action);

		PayloadResolver resolver = payloadPluginService.getPayloadResolver(payloadClaims);

		assertThat(resolver, is(instanceOf(OnlyOfficePayloadResolver.class)));
		assertThat(resolver.getUserId(payloadClaims), is("user"));
	}

}
