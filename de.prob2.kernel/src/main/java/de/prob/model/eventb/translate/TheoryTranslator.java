package de.prob.model.eventb.translate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import de.be4.classicalb.core.parser.analysis.prolog.ASTProlog;

import de.prob.animator.domainobjects.EventB;
import de.prob.model.eventb.EventBAxiom;
import de.prob.model.eventb.theory.AxiomaticDefinitionBlock;
import de.prob.model.eventb.theory.DataType;
import de.prob.model.eventb.theory.DirectDefinition;
import de.prob.model.eventb.theory.IOperatorDefinition;
import de.prob.model.eventb.theory.Operator;
import de.prob.model.eventb.theory.OperatorArgument;
import de.prob.model.eventb.theory.RecursiveDefinitionCase;
import de.prob.model.eventb.theory.RecursiveOperatorDefinition;
import de.prob.model.eventb.theory.Theory;
import de.prob.model.eventb.theory.Type;
import de.prob.model.representation.ModelElementList;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.tmparser.OperatorMapping;
import de.prob.util.Tuple2;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.extension.IFormulaExtension;

public class TheoryTranslator {

	private final List<Theory> theories;
	private Set<IFormulaExtension> typeEnv;

	public TheoryTranslator(final ModelElementList<Theory> theories) {
		this.theories = new ArrayList<>();

		for (Theory theory : theories) {
			if (!this.theories.contains(theory)) {
				for (Theory t : theory.getImported()) {
					if (!this.theories.contains(t)) {
						this.theories.add(t);
					}
				}
				this.theories.add(theory);
			}
		}
	}

	public void toProlog(final IPrologTermOutput pto) {
		for (Theory theory : theories) {
			typeEnv = theory.getTypeEnvironment();
			pto.openTerm("theory");
			printTheoryName(theory, pto);
			printListOfImportedTheories(theory.getImported(), pto);
			printTypeParameters(theory.getTypeParameters(), pto);
			printDataTypes(theory.getDataTypes(), pto);
			printOperatorDefs(theory.getOperators(), pto);
			printAxiomaticDefintionBlocks(
					theory.getAxiomaticDefinitionBlocks(), pto);
			printMappings(theory.getProBMappings(), pto);
			pto.closeTerm();
		}
	}

	public void printTheoryName(final Theory t, final IPrologTermOutput pto) {
		pto.openTerm("theory_name");
		pto.printAtom(t.getParentDirectoryName());
		pto.printAtom(t.getName());
		pto.closeTerm();
	}

	private void printListOfImportedTheories(
			final ModelElementList<Theory> imported, final IPrologTermOutput pto) {
		pto.openList();
		for (Theory theory : imported) {
			printTheoryName(theory, pto);
		}
		pto.closeList();
	}

	private void printTypeParameters(
			final ModelElementList<Type> typeParameters,
			final IPrologTermOutput pto) {
		pto.openList();
		for (Type t : typeParameters) {
			pto.printAtom(t.toString());
		}
		pto.closeList();
	}

	private void printDataTypes(final ModelElementList<DataType> dataTypes,
			final IPrologTermOutput pto) {
		pto.openList();
		for (DataType dataType : dataTypes) {
			printDataType(dataType, pto);
		}
		pto.closeList();
	}

	private void printDataType(final DataType dataType,
			final IPrologTermOutput pto) {
		pto.openTerm("datatype");
		pto.printAtom(dataType.toString());
		pto.openList();
		for (String arg : dataType.getTypeArguments()) {
			printType(arg, pto);
		}
		pto.closeList();
		pto.openList();
		for (Entry<String, List<Tuple2<String, String>>> cons : dataType
				.getConstructors().entrySet()) {
			printConstructor(cons.getKey(), cons.getValue(), pto);
		}
		pto.closeList();
		pto.closeTerm();
	}

	private void printType(final String type, final IPrologTermOutput pto) {
		printEventBElement(new EventB(type, typeEnv), pto);
	}

	private void printConstructor(String name,
			List<Tuple2<String, String>> destructors,
			final IPrologTermOutput pto) {
		pto.openTerm("constructor");
		pto.printAtom(name);
		pto.openList();
		for (Tuple2<String, String> arg : destructors) {
			printTypedIdentifier("destructor", arg.getFirst(),
					new EventB(arg.getSecond(), typeEnv), pto);
		}
		pto.closeList();
		pto.closeTerm();
	}

	private void printTypedIdentifier(final String functor,
			final String idString, final EventB type,
			final IPrologTermOutput pto) {
		pto.openTerm(functor);
		pto.printAtom(idString);
		printEventBElement(type, pto);
		pto.closeTerm();
	}

	private void printEventBElement(final EventB eventB,
			final IPrologTermOutput pto) {
		eventB.getAst().apply(new ASTProlog(pto, null));
	}

	private void printOperatorDefs(final ModelElementList<Operator> operators,
			final IPrologTermOutput pto) {
		pto.openList();
		for (Operator operator : operators) {
			printOperator(operator, pto);
		}
		pto.closeList();
	}

	private void printOperator(final Operator operator,
			final IPrologTermOutput pto) {
		pto.openTerm("operator");
		pto.printAtom(operator.toString());

		printOperatorArguments(operator.getArguments(), pto);
		printEventBElement(operator.getWD(), pto);

		processDefinition(operator.getDefinition(), pto);

		pto.closeTerm();
	}

	private void processDefinition(final IOperatorDefinition definition,
			final IPrologTermOutput pto) {
		if (definition instanceof DirectDefinition) {
			printDirectDefinition((DirectDefinition) definition, pto);

			// Empty list for recursive definitions
			pto.openList();
			pto.closeList();
		}
		if (definition instanceof RecursiveOperatorDefinition) {
			// Empty list for direct definitions
			pto.openList();
			pto.closeList();

			printRecursiveDefinition((RecursiveOperatorDefinition) definition,
					pto);
		}
	}

	private void printRecursiveDefinition(
			final RecursiveOperatorDefinition definition,
			final IPrologTermOutput pto) {
		EventB inductiveArgument = definition.getInductiveArgument();
		pto.openList();
		ModelElementList<RecursiveDefinitionCase> cases = definition.getCases();
		for (RecursiveDefinitionCase c : cases) {
			printRecursiveCase(inductiveArgument, c, pto);
		}
		pto.closeList();
	}

	private void printRecursiveCase(final EventB inductiveArgument,
			final RecursiveDefinitionCase c, final IPrologTermOutput pto) {
		c.getExpression();
		c.getFormula();

		pto.openTerm("case");
		pto.printAtom(inductiveArgument.getCode());
		pto.openList();
		Expression expression = c.getExpression().getRodinParsedResult()
				.getParsedExpression();
		for (FreeIdentifier fi : expression.getFreeIdentifiers()) {
			pto.printAtom(fi.getName());
		}
		pto.closeList();
		printEventBElement(c.getExpression(), pto);
		printEventBElement(c.getFormula(), pto);
		pto.closeTerm();
	}

	private void printDirectDefinition(final DirectDefinition definition,
			final IPrologTermOutput pto) {
		pto.openList();
		printEventBElement((EventB) definition.getFormula(), pto);
		pto.closeList();
	}

	private void printOperatorArguments(final List<OperatorArgument> arguments,
			final IPrologTermOutput pto) {
		pto.openList();
		for (OperatorArgument argument : arguments) {
			printTypedIdentifier("argument", argument.getIdentifier()
					.toString(), argument.getType(), pto);
		}
		pto.closeList();
	}

	private void printAxiomaticDefintionBlocks(
			final ModelElementList<AxiomaticDefinitionBlock> axiomaticDefinitionBlocks,
			final IPrologTermOutput pto) {
		pto.openList();
		for (AxiomaticDefinitionBlock block : axiomaticDefinitionBlocks) {
			printAxiomaticDefinitonBlock(block, pto);
		}
		pto.closeList();
	}

	private void printAxiomaticDefinitonBlock(
			final AxiomaticDefinitionBlock block, final IPrologTermOutput pto) {
		pto.openTerm("axiomatic_def_block");
		pto.printAtom(block.getName());

		printTypeParameters(block.getTypeParameters(), pto);

		pto.openList();
		for (Operator operator : block.getOperators()) {
			printAxiomaticOperator(operator, pto);
		}
		pto.closeList();

		pto.openList();
		for (EventBAxiom axiom : block.getAxioms()) {
			printEventBElement((EventB) axiom.getPredicate(), pto);
		}
		pto.closeList();

		pto.closeTerm();
	}

	private void printAxiomaticOperator(final Operator operator,
			final IPrologTermOutput pto) {
		pto.openTerm("opdef");
		pto.printAtom(operator.toString());
		printOperatorArguments(operator.getArguments(), pto);
		pto.openList();
		printEventBElement(operator.getWD(), pto);
		pto.closeList();
		pto.closeTerm();
	}

	private void printMappings(final Collection<OperatorMapping> proBMappings,
			final IPrologTermOutput pto) {
		pto.openList();
		// Currently, we support only one kind of operator mapping, just tagging
		// an operator to indicate that an optimized ProB implementation should
		// be used. We do not invest any effort in preparing future kinds of
		// other operator mappings.
		for (OperatorMapping mapping : proBMappings) {
			pto.openTerm("tag");
			pto.printAtom(mapping.getOperatorName());
			pto.printAtom(mapping.getSpec());
			pto.closeTerm();
		}
		pto.closeList();
	}

}
