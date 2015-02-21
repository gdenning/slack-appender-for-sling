package com.zerodes.slackappender;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.jackson.JacksonFeature;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;

/**
 * Created by geoff on 20/02/15.
 */
public class SlackLogbackAppender extends AppenderBase<ILoggingEvent> {

	private static final String ICON_EMOJI = ":heavy_exclamation_mark:";
	private static final String USERNAME = "sling-logback-appender";

	private Client jaxrsClient;

	@Override
	public void start() {
		super.start();
		jaxrsClient = ClientBuilder.newClient().register(JacksonFeature.class);
	}

	@Override
	protected void append(final ILoggingEvent iLoggingEvent) {
		SlackWebhookEntity slackWebhookEntity = new SlackWebhookEntity();
		slackWebhookEntity.setUsername(USERNAME);
		slackWebhookEntity.setIconEmoji(ICON_EMOJI);
		slackWebhookEntity.setText(iLoggingEvent.getMessage());
		jaxrsClient.target("https://hooks.slack.com/services/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")
				.request()
				.post(Entity.entity(slackWebhookEntity, MediaType.APPLICATION_JSON_TYPE));
	}

	@Override
	public synchronized void doAppend(ILoggingEvent eventObject) {
		super.doAppend(eventObject);
	}

	@Override
	public String getName() {
		return "SlackLogbackAppender";
	}
}
