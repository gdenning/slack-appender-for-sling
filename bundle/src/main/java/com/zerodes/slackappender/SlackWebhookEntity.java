package com.zerodes.slackappender;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by geoff on 20/02/15.
 */
public class SlackWebhookEntity {
	@JsonProperty("text")
	private String text;

	@JsonProperty("username")
	private String username;

	@JsonProperty("icon_emoji")
	private String iconEmoji;

	public void setText(final String text) {
		this.text = text;
	}

	public void setUsername(final String username) {
		this.username = username;
	}

	public void setIconEmoji(final String iconEmoji) {
		this.iconEmoji = iconEmoji;
	}
}
