package de.prob.web

import javax.servlet.ServletException
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import com.google.inject.Inject
import com.google.inject.Singleton

@Singleton
class ExpServlet extends HttpServlet {



	def random = new Random()

	@Inject
	public ExpServlet() {
		super();
	}

	def generator = { n->
		if (n == null) "foo" else
			random.with {
				(1..n).collect {
					(('A'..'Z')+('0'..'9')).join()[ nextInt( (('A'..'Z')+('0'..'9')).join().length() ) ]
				}.join()
			}
	}

	def i = 0;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
	throws ServletException, IOException {

		i++;

		def tt = [
			[["count"], i],
			[
				["random"],
				{ v ->
					if (i % 5 == 0) random.nextInt(10000) else v
				}
			],
			[
				["a", "b", "c"],
				{ v,s ->
					if (s != null && s.random != null && s.random % 2 == 0) generator(random.nextInt(10)) else "foo"
				}]
		]
		def s = UIState.transact(tt);
		send(resp,s as String)
	}

	private void send(final HttpServletResponse resp, final String html)
	throws IOException {
		PrintWriter writer = resp.getWriter();
		writer.write(html);
		writer.flush();
		writer.close();
	}
}
