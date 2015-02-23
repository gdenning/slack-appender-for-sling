package com.zerodes.slackappender.service;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.PropertyOption;
import org.apache.felix.scr.annotations.PropertyUnbounded;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.osgi.service.component.ComponentContext;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.pattern.ThrowableProxyConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.core.CoreConstants;

import com.zerodes.slackappender.jersey.SlackWebhookEntity;

@Component(enabled = true, immediate = true, metatype = true, label = "Slack Logback Appender")
@Service(Appender.class)
public class SlackLogbackAppender extends AppenderBase<ILoggingEvent> {
	private static final Map<Level, String> levelEmojiMap = new HashMap<Level, String>();
	private static final String USERNAME = "sling-logback-appender";

	@Property(unbounded = PropertyUnbounded.ARRAY, description = "Loggers to attach this appender to.",
			value = { "com.day.cq" })
	private static final String CONFIG_LOGGERS = "loggers";

	@Property(description = "Minimum logging level of events to send to Slack.", options = {
			@PropertyOption(name = "TRACE", value = "TRACE"),
			@PropertyOption(name = "DEBUG", value = "DEBUG"),
			@PropertyOption(name = "INFO", value = "INFO"),
			@PropertyOption(name = "WARN", value = "WARN"),
			@PropertyOption(name = "ERROR", value = "ERROR") })
	private static final String CONFIG_MIN_LEVEL = "min.level";

	@Property(description = "Level of detail to include in stack traces.", options = {
			@PropertyOption(name = "CAUSED_BY", value = "CAUSED_BY"),
			@PropertyOption(name = "FULL", value = "FULL") })
	private static final String CONFIG_STACK_TRACE_TYPE = "stack.trace.type";

	@Property(description = "Slack webhook URL.")
	private static final String CONFIG_WEBHOOK_URL = "webhook.url";

	private Client jaxrsClient;

	private String minLevel;

	private String stackTraceType;

	private String webhookUrl;

	{
		levelEmojiMap.put(Level.ERROR, ":heavy_exclamation_mark:");
		levelEmojiMap.put(Level.WARN, ":warning:");
		levelEmojiMap.put(Level.INFO, ":information_source:");
		levelEmojiMap.put(Level.DEBUG, ":large_blue_circle:");
		levelEmojiMap.put(Level.TRACE, ":white_circle:");
	}

	/**
	 * Activates & loads all properties.
	 *
	 * @param ctx Context.
	 * @throws Exception exception
	 */
	@Activate
	protected void activate(final ComponentContext ctx) throws Exception {
		final Dictionary<?, ?> props = ctx.getProperties();
		minLevel = PropertiesUtil.toString(props.get(CONFIG_MIN_LEVEL), "WARN");
		stackTraceType = PropertiesUtil.toString(props.get(CONFIG_STACK_TRACE_TYPE), "CAUSED_BY");
		webhookUrl = PropertiesUtil.toString(props.get(CONFIG_WEBHOOK_URL), "");
	}

	@Override
	public void start() {
		super.start();
		jaxrsClient = ClientBuilder.newClient().register(JacksonFeature.class);
	}

	@Override
	public void stop() {
		super.stop();
		jaxrsClient = null;
	}

	@Override
	protected void append(final ILoggingEvent iLoggingEvent) {
		if (iLoggingEvent.getLevel().isGreaterOrEqual(Level.toLevel(minLevel))) {
			String hostName;
			try {
				hostName = InetAddress.getLocalHost().getHostName();
			} catch (UnknownHostException e) {
				hostName = "Unknown host";
			}
			SlackWebhookEntity slackWebhookEntity = new SlackWebhookEntity();
			slackWebhookEntity.setUsername(USERNAME + ":" + hostName + ":" + iLoggingEvent.getLevel().toString());
			slackWebhookEntity.setIconEmoji(levelEmojiMap.get(iLoggingEvent.getLevel()));
			slackWebhookEntity.setText(hostName + ": "
					+ iLoggingEvent.getLevel().toString() + ": "
					+ iLoggingEvent.getFormattedMessage() + "\n"
					+ convertThrowableToStackTraceString(iLoggingEvent));
			jaxrsClient.target(webhookUrl)
					.request()
					.post(Entity.entity(slackWebhookEntity, MediaType.APPLICATION_JSON_TYPE));
		}
	}

	private String convertThrowableToStackTraceString(final ILoggingEvent loggingEvent) {
		StringBuffer result = new StringBuffer();
		if (loggingEvent.getThrowableProxy() != null) {
			List<String> stackOptionList = new ArrayList<String>();
			if (stackTraceType.equalsIgnoreCase("FULL")) {
				stackOptionList.add("full");
			}
			ThrowableProxyConverter converter = new ThrowableProxyConverter();
			converter.setOptionList(stackOptionList);
			result.append(converter.convert(loggingEvent));
			result.append(CoreConstants.LINE_SEPARATOR);
		}
		return result.toString();
	}

	@Override
	public String getName() {
		return "SlackLogbackAppender";
	}
}
