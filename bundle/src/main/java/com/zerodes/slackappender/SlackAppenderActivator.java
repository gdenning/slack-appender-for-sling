package com.zerodes.slackappender;

import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import ch.qos.logback.core.Appender;

/**
 * OSGi bundle activator for Slack appender.
 */
public class SlackAppenderActivator implements BundleActivator {

	private ServiceRegistration serviceRegistration;

	@Override
	public void start(final BundleContext bundleContext) throws Exception {
		Dictionary<String, Object> props = new Hashtable<String, Object>();
		String[] loggers = { "com.elasticpath.devry.commerce.impl.DeVryElasticPathCommerceSessionImpl:DEBUG", "org.apache.sling:ERROR" };
		props.put("loggers", loggers);

		serviceRegistration = bundleContext.registerService(Appender.class.getName(), new SlackLogbackAppender(), props);
	}

	@Override
	public void stop(final BundleContext bundleContext) throws Exception {
		serviceRegistration.unregister();
	}
}