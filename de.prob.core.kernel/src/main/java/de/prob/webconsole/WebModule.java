package de.prob.webconsole;

import com.google.inject.servlet.ServletModule;

import de.prob.testing.ProBTestRunner;
import de.prob.testing.TestRegistry;
import de.prob.webconsole.servlets.CompletionServlet;
import de.prob.webconsole.servlets.GroovyBindingsServlet;
import de.prob.webconsole.servlets.GroovyOutputServlet;
import de.prob.webconsole.servlets.GroovyShellServlet;
import de.prob.webconsole.servlets.ImportsServlet;
import de.prob.webconsole.servlets.LogServlet;
import de.prob.webconsole.servlets.ScrollbackServlet;
import de.prob.webconsole.servlets.VersionServlet;
import de.prob.webconsole.servlets.visualizations.PredicateServlet;
import de.prob.webconsole.servlets.visualizations.StateSpaceServlet;
import de.prob.webconsole.servlets.visualizations.ValueOverTimeServlet;
import de.prob.worksheet.servlets.CloseDocument;
import de.prob.worksheet.servlets.LoadDocument;
import de.prob.worksheet.servlets.NewBlock;
import de.prob.worksheet.servlets.NewDocument;
import de.prob.worksheet.servlets.SaveDocument;
import de.prob.worksheet.servlets.eval.Evaluate;
import de.prob.worksheet.servlets.eval.SwitchBlock;
import de.prob.worksheet.servlets.sync.GetBlock;
import de.prob.worksheet.servlets.sync.SetBlock;

public class WebModule extends ServletModule {

	@Override
	protected void configureServlets() {
		super.configureServlets();
		serve("/evaluate*").with(GroovyShellServlet.class);
		serve("/bindings*").with(GroovyBindingsServlet.class);
		serve("/complete*").with(CompletionServlet.class);
		serve("/imports*").with(ImportsServlet.class);
		serve("/outputs*").with(GroovyOutputServlet.class);
		serve("/versions*").with(VersionServlet.class);
		serve("/scrollback*").with(ScrollbackServlet.class);

		// Worksheet servlets
		serve("/saveDocument*").with(SaveDocument.class);
		serve("/newDocument*").with(NewDocument.class);
		serve("/loadDocument*").with(LoadDocument.class);
		serve("/closeDocument*").with(CloseDocument.class);
		serve("/newBlock*").with(NewBlock.class);
		serve("/worksheetEvaluate*").with(Evaluate.class);
		serve("/setBlock*").with(SetBlock.class);

		// unused
		serve("/switchBlock*").with(SwitchBlock.class);
		serve("/getBlock*").with(GetBlock.class);

		bind(ShellCommands.class);
		bind(OutputBuffer.class);
		bind(ProBTestRunner.class);
		bind(TestRegistry.class);

		// logging
		serve("/get_log*").with(LogServlet.class);

		serve("/formula*").with(ValueOverTimeServlet.class);
		serve("/statespace_servlet*").with(StateSpaceServlet.class);
		serve("/predicate*").with(PredicateServlet.class);
	}
}
