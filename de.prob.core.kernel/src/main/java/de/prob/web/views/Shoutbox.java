package de.prob.web.views;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.AsyncContext;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import de.prob.annotations.PublicSession;
import de.prob.web.AbstractSession;
import de.prob.web.WebUtils;

@PublicSession
@Singleton
public class Shoutbox extends AbstractSession {

	private final List<Shout> texts = new ArrayList<Shout>();

	@Inject
	public Shoutbox() {
		incrementalUpdate = false;
	}

	@SuppressWarnings("unused")
	private static class Shout {
		public final String text;
		public final Date time;

		public Shout(final String text) {
			this.text = text;
			time = Calendar.getInstance().getTime();
		}
	}

	public Object addText(final Map<String, String[]> params) {
		String text = params.get("text")[0];
		Shout shout = new Shout(text);
		texts.add(shout);
		return WebUtils.wrap("cmd", "Shoutbox.append", "line",
				WebUtils.toJson(shout));
	}

	@Override
	public String html(final String clientid,
			final Map<String, String[]> parameterMap) {
		return simpleRender(clientid, "ui/shoutbox/index.html");
	}

	@Override
	public void reload(final String client, final int lastinfo,
			final AsyncContext context) {
		sendInitMessage(context);
		Map<String, String> wrap = WebUtils.wrap("cmd", "Shoutbox.setText",
				"texts", WebUtils.toJson(texts));
		submit(wrap);
	}

}
