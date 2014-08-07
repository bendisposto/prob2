package de.prob.model.eventb.translate;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import de.prob.animator.domainobjects.EventB;
import de.prob.model.eventb.EventBAxiom;
import de.prob.model.eventb.EventBModel;
import de.prob.model.eventb.theory.AxiomaticDefinitionBlock;
import de.prob.model.eventb.theory.DataType;
import de.prob.model.eventb.theory.DataTypeConstructor;
import de.prob.model.eventb.theory.DataTypeDestructor;
import de.prob.model.eventb.theory.DirectDefinition;
import de.prob.model.eventb.theory.IOperatorDefinition;
import de.prob.model.eventb.theory.InferenceRule;
import de.prob.model.eventb.theory.MetaVariable;
import de.prob.model.eventb.theory.Operator;
import de.prob.model.eventb.theory.OperatorArgument;
import de.prob.model.eventb.theory.ProofRulesBlock;
import de.prob.model.eventb.theory.RecursiveDefinitionCase;
import de.prob.model.eventb.theory.RecursiveOperatorDefinition;
import de.prob.model.eventb.theory.RewriteRule;
import de.prob.model.eventb.theory.RewriteRuleRHS;
import de.prob.model.eventb.theory.Theory;
import de.prob.model.eventb.theory.Type;
import de.prob.model.representation.ModelElementList;
import de.prob.tmparser.OperatorMapping;
import de.prob.tmparser.TheoryMappingException;
import de.prob.tmparser.TheoryMappingParser;

public class TheoryXmlHandler extends DefaultHandler {

	Logger logger = LoggerFactory.getLogger(TheoryXmlHandler.class);

	private final String workspacePath;
	private final Set<IFormulaExtension> typeEnv = new HashSet<IFormulaExtension>();
	private final EventBModel model;
	private final ModelElementList<Theory> theories = new ModelElementList<Theory>();
	private final HashMap<String, Theory> theoryMap = new HashMap<String, Theory>();

	public TheoryXmlHandler(final EventBModel model, final String workspacePath) {
		this.model = model;
		this.workspacePath = workspacePath;
	}

	@Override
	public void startElement(final String uri, final String localName,
			final String qName, final Attributes attributes)
			throws SAXException {
		if (qName.equals("org.eventb.theory.core.scAvailableTheory")) {
			String path = attributes
					.getValue("org.eventb.theory.core.availableTheory");
			path = path.substring(0, path.indexOf('|'));

			if (!theoryMap.containsKey(path)) {
				try {
					SAXParserFactory parserFactory = SAXParserFactory
							.newInstance();
					SAXParser saxParser = parserFactory.newSAXParser();

					TheoryExtractor extractor = new TheoryExtractor(path);
					saxParser.parse(new File(workspacePath + path), extractor);
					theories.add(extractor.getTheory());
				} catch (ParserConfigurationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				theories.add(theoryMap.get(path));
			}
		}
	}

	public Set<IFormulaExtension> getTypeEnv() {
		return typeEnv;
	}

	@Override
	public void endDocument() throws SAXException {
		model.addTheories(theories);
	}

	class TheoryExtractor extends DefaultHandler {

		private final Theory theory;
		private final ModelElementList<Theory> imported = new ModelElementList<Theory>();
		private final ModelElementList<Type> typeParameters = new ModelElementList<Type>();
		private final ModelElementList<DataType> dataTypes = new ModelElementList<DataType>();
		private final ModelElementList<Operator> operators = new ModelElementList<Operator>();
		private final ModelElementList<AxiomaticDefinitionBlock> axiomaticDefinitionsBlocks = new ModelElementList<AxiomaticDefinitionBlock>();
		private final ModelElementList<EventBAxiom> theorems = new ModelElementList<EventBAxiom>();
		private final ModelElementList<ProofRulesBlock> proofRules = new ModelElementList<ProofRulesBlock>();

		// For adding DataType
		private DataType dataType;
		private ModelElementList<DataTypeConstructor> constructors;
		private ModelElementList<Type> typeArguments; // Also used for axiomatic
														// definition blocks

		// For adding DataType constructors
		private DataTypeConstructor constructor;
		private ModelElementList<DataTypeDestructor> destructors;

		// For adding Operator
		private Operator operator;
		private ModelElementList<OperatorArgument> opArgs;

		// For adding definition
		private IOperatorDefinition definition;

		// If recursive definition cases arise
		private ModelElementList<RecursiveDefinitionCase> recursiveDefinitions;

		// For adding axiomatic definitions
		private AxiomaticDefinitionBlock axiomaticDefinitionBlock;
		private Operator axiomaticOperator;
		private ModelElementList<Operator> axiomaticOperators;
		private ModelElementList<EventBAxiom> definitionAxioms;

		// For adding proof rules block
		private ProofRulesBlock block;
		private ModelElementList<MetaVariable> metaVars;
		private ModelElementList<RewriteRule> rewriteRules;
		private ModelElementList<InferenceRule> inferenceRules;

		// For rewrite rules
		private RewriteRule rewriteRule;
		private ModelElementList<RewriteRuleRHS> rightHandSides;

		// For inference rules
		private List<EventB> given;
		private EventB infer;

		public TheoryExtractor(final String path) {
			String dir = path.substring(path.indexOf('/') + 1,
					path.lastIndexOf('/'));
			String name = path.substring(path.lastIndexOf('/') + 1,
					path.lastIndexOf('.'));
			Collection<OperatorMapping> mappings = new ArrayList<OperatorMapping>();
			try {
				String mappingFileName = workspacePath + File.separator + dir
						+ File.separator + name + ".ptm";
				mappings = TheoryMappingParser.parseTheoryMapping(name,
						mappingFileName);
			} catch (FileNotFoundException e) {
				logger.warn("No .ptm file found for Theory "
						+ name
						+ ". This means that ProB has no information on how to interpret this theory.");
			} catch (TheoryMappingException e) {

				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			theory = new Theory(name, dir, mappings);
			theoryMap.put(path, theory);
		}

		public Theory getTheory() {
			return theory;
		}

		@Override
		public void startElement(final String uri, final String localName,
				final String qName, final Attributes attributes)
				throws SAXException {
			if (qName.equals("org.eventb.theory.core.scTypeParameter")) {
				addTypeParameter(attributes);
			} else if (qName.equals("org.eventb.theory.core.useTheory")) {
				addUsedTheory(attributes);
			} else if (qName
					.equals("org.eventb.theory.core.scDatatypeDefinition")) {
				beginAddingDataType(attributes);
			} else if (qName
					.equals("org.eventb.theory.core.scDatatypeConstructor")) {
				beginAddingDataTypeConstructor(attributes);
			} else if (qName
					.equals("org.eventb.theory.core.scConstructorArgument")) {
				addDestructor(attributes);
			} else if (qName
					.equals("org.eventb.theory.core.scNewOperatorDefinition")) {
				beginAddingOperator(attributes);
			} else if (qName
					.equals("org.eventb.theory.core.scDirectOperatorDefinition")) {
				addDirectDefinition(attributes);
			} else if (qName
					.equals("org.eventb.theory.core.scRecursiveOperatorDefinition")) {
				beginRecursiveOpDef(attributes);
			} else if (qName
					.equals("org.eventb.theory.core.scRecursiveDefinitionCase")) {
				addRecursiveDefinitionCase(attributes);
			} else if (qName
					.equals("org.eventb.theory.core.scNewOperatorDefinition")) {
				addDirectDefinition(attributes);
			} else if (qName
					.equals("org.eventb.theory.core.scOperatorArgument")) {
				addOperatorArgument(attributes);
			} else if (qName
					.equals("org.eventb.theory.core.scAxiomaticDefinitionsBlock")) {
				addAxiomaticDefinitionBlock(attributes);
			} else if (qName
					.equals("org.eventb.theory.core.scAxiomaticOperatorDefinition")) {
				beginAddingAxiomaticOperator(attributes);
			} else if (qName
					.equals("org.eventb.theory.core.scAxiomaticDefinitionAxiom")) {
				addDefinitionAxiom(attributes);
			} else if (qName
					.equals("org.eventb.theory.core.scAxiomaticTypeDefinition")) {
				addTypeParameter(attributes);
			} else if (qName.equals("org.eventb.theory.core.scTheorem")) {
				addTheorem(attributes);
			} else if (qName.equals("org.eventb.theory.core.scProofRulesBlock")) {
				beginProofRulesBlock(attributes);
			} else if (qName.equals("org.eventb.theory.core.scMetavariable")) {
				addMetaVariable(attributes);
			} else if (qName.equals("org.eventb.theory.core.scRewriteRule")) {
				beginRewriteRule(attributes);
			} else if (qName.equals("org.eventb.theory.core.scRewriteRuleRHS")) {
				addRightHandSide(attributes);
			} else if (qName.equals("org.eventb.theory.core.scInferenceRule")) {
				beginInferenceRule(attributes);
			} else if (qName.equals("org.eventb.theory.core.scInfer")) {
				addInfer(attributes);
			} else if (qName.equals("org.eventb.theory.core.scGiven")) {
				addGiven(attributes);
			}
		}

		private void addGiven(final Attributes attributes) {
			String predicate = attributes.getValue("org.eventb.core.predicate");
			given.add(new EventB(predicate, typeEnv));
		}

		private void addInfer(final Attributes attributes) {
			String predicate = attributes.getValue("org.eventb.core.predicate");
			infer = new EventB(predicate, typeEnv);
		}

		private void beginInferenceRule(final Attributes attributes) {
			given = new ArrayList<EventB>();
		}

		private void addRightHandSide(final Attributes attributes) {
			String name = attributes.getValue("org.eventb.core.label");
			String predicate = attributes.getValue("org.eventb.core.predicate");
			String formula = attributes
					.getValue("org.eventb.theory.core.formula");
			rightHandSides.add(new RewriteRuleRHS(name, predicate, formula,
					typeEnv));
		}

		private void beginRewriteRule(final Attributes attributes) {
			String label = attributes.getValue("org.eventb.core.label");
			String applicability = attributes
					.getValue("org.eventb.theory.core.applicability");
			boolean complete = "true".equals(attributes
					.getValue("org.eventb.theory.core.complete"));
			String desc = attributes.getValue("org.eventb.theory.core.desc");
			String formula = attributes
					.getValue("org.eventb.theory.core.formula");
			rewriteRule = new RewriteRule(label, applicability, complete, desc,
					formula, typeEnv);

			rightHandSides = new ModelElementList<RewriteRuleRHS>();
			rewriteRules.add(rewriteRule);
		}

		private void addMetaVariable(final Attributes attributes) {
			String name = attributes.getValue("name");
			String type = attributes.getValue("org.eventb.core.type");
			metaVars.add(new MetaVariable(name, type, typeEnv));
		}

		private void beginProofRulesBlock(final Attributes attributes) {
			String name = attributes.getValue("org.eventb.core.label");
			if (name == null) {
				name = attributes.getValue("name");
			}
			block = new ProofRulesBlock(name);

			metaVars = new ModelElementList<MetaVariable>();
			rewriteRules = new ModelElementList<RewriteRule>();
			inferenceRules = new ModelElementList<InferenceRule>();

			proofRules.add(block);
		}

		private void addTheorem(final Attributes attributes) {
			String label = attributes.getValue("org.eventb.core.label");
			String predicate = attributes.getValue("org.eventb.core.predicate");

			theorems.add(new EventBAxiom(label, predicate, true, typeEnv));
		}

		private void addAxiomaticDefinitionBlock(final Attributes attributes) {
			String name = attributes.getValue("org.eventb.core.label");
			axiomaticDefinitionBlock = new AxiomaticDefinitionBlock(name);
			typeArguments = new ModelElementList<Type>();
			axiomaticOperators = new ModelElementList<Operator>();
			definitionAxioms = new ModelElementList<EventBAxiom>();

			axiomaticDefinitionsBlocks.add(axiomaticDefinitionBlock);
		}

		private void beginAddingAxiomaticOperator(final Attributes attributes) {
			axiomaticOperator = createOperator(attributes);
			axiomaticOperators.add(axiomaticOperator);

			opArgs = new ModelElementList<OperatorArgument>();
		}

		private void addDefinitionAxiom(final Attributes attributes) {
			String label = attributes.getValue("org.eventb.core.label");
			String predicate = attributes.getValue("org.eventb.core.predicate");

			definitionAxioms.add(new EventBAxiom(label, predicate, false,
					typeEnv));
		}

		private void addOperatorArgument(final Attributes attributes) {
			String identifier = attributes.getValue("name");
			String type = attributes.getValue("org.eventb.core.type");
			opArgs.add(new OperatorArgument(identifier, type, typeEnv));
		}

		private void addDirectDefinition(final Attributes attributes) {
			String formula = attributes
					.getValue("org.eventb.theory.core.formula");
			definition = new DirectDefinition(formula, typeEnv);
		}

		private void addRecursiveDefinitionCase(final Attributes attributes) {
			String expression = attributes
					.getValue("org.eventb.core.expression");
			String formula = attributes
					.getValue("org.eventb.theory.core.formula");
			recursiveDefinitions.add(new RecursiveDefinitionCase(expression,
					formula));
		}

		private void beginRecursiveOpDef(final Attributes attributes) {
			String indArg = attributes
					.getValue("org.eventb.theory.core.inductiveArgument");
			definition = new RecursiveOperatorDefinition(indArg, typeEnv);

			recursiveDefinitions = new ModelElementList<RecursiveDefinitionCase>();
		}

		private void beginAddingOperator(final Attributes attributes) {
			operator = createOperator(attributes);
			operators.add(operator);

			opArgs = new ModelElementList<OperatorArgument>();
		}

		private Operator createOperator(final Attributes attributes) {
			String label = attributes.getValue("org.eventb.core.label");
			boolean associative = "true".equals(attributes
					.getValue("org.eventb.theory.core.associative"));
			boolean commutative = "true".equals(attributes
					.getValue("org.eventb.theory.core.commutative"));
			boolean formulaType = "true".equals(attributes
					.getValue("org.eventb.theory.core.formulaType"));
			String notationType = attributes
					.getValue("org.eventb.theory.core.notationType");
			String groupId = attributes
					.getValue("org.eventb.theory.core.groupID");
			String predicate = attributes.getValue("org.eventb.core.predicate");
			String type = attributes.getValue("org.eventb.theory.core.type");
			String wd = attributes.getValue("org.eventb.theory.core.wd");
			return new Operator(theory.getName(), label, associative,
					commutative, formulaType, notationType, groupId, type, wd,
					predicate, typeEnv);
		}

		private void addDestructor(final Attributes attributes) {
			String name = attributes.getValue("name");
			String type = attributes.getValue("org.eventb.core.type");

			destructors.add(new DataTypeDestructor(name, type));
		}

		private void beginAddingDataTypeConstructor(final Attributes attributes) {
			String name = attributes.getValue("name");
			constructor = new DataTypeConstructor(name);

			constructors.add(constructor);

			destructors = new ModelElementList<DataTypeDestructor>();
		}

		private void beginAddingDataType(final Attributes attributes) {
			String name = attributes.getValue("name");
			dataType = new DataType(name);

			dataTypes.add(dataType);

			constructors = new ModelElementList<DataTypeConstructor>();
			typeArguments = new ModelElementList<Type>();

		}

		private void addUsedTheory(final Attributes attributes)
				throws SAXException {
			String target = attributes.getValue("org.eventb.core.scTarget");
			String path = target.substring(0, target.indexOf('|'));
			if (theoryMap.containsKey(path)) {
				imported.add(theoryMap.get(path));
			} else {
				try {
					SAXParserFactory parserFactory = SAXParserFactory
							.newInstance();
					SAXParser saxParser = parserFactory.newSAXParser();

					TheoryExtractor extractor = new TheoryExtractor(path);
					saxParser.parse(new File(workspacePath + path), extractor);
					theories.add(extractor.getTheory());
				} catch (ParserConfigurationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		private void addTypeParameter(final Attributes attributes) {
			String name = attributes.getValue("name");
			Type p = new Type(name, typeEnv);
			typeParameters.add(p);
		}

		@Override
		public void endElement(final String uri, final String localName,
				final String qName) throws SAXException {
			if (qName.equals("org.eventb.theory.core.scDatatypeDefinition")) {
				finishDataType();
			} else if (qName
					.equals("org.eventb.theory.core.scDatatypeConstructor")) {
				constructor.addDestructors(destructors);
			} else if (qName
					.equals("org.eventb.theory.core.scNewOperatorDefinition")) {
				finishOperator();
			} else if (qName
					.equals("org.eventb.theory.core.scRecursiveDefinitionCase")) {
				finishRecursiveDefinition();
			} else if (qName
					.equals("org.eventb.theory.core.scAxiomaticOperatorDefinition")) {
				finishAxiomaticOperator();
			} else if (qName
					.equals("org.eventb.theory.core.scAxiomaticDefinitionsBlock")) {
				finishAxiomaticDefinitionBlock();
			} else if (qName.equals("org.eventb.theory.core.scProofRulesBlock")) {
				finishProofRulesBlock();
			} else if (qName.equals("org.eventb.theory.core.scRewriteRule")) {
				finishRewriteRule();
			} else if (qName.equals("org.eventb.theory.core.scInferenceRule")) {
				finishInferenceRule();
			}
		}

		private void finishAxiomaticDefinitionBlock() {
			axiomaticDefinitionBlock.addDefinitionAxioms(definitionAxioms);
			axiomaticDefinitionBlock.addOperators(axiomaticOperators);
			axiomaticDefinitionBlock.addTypeParameters(typeArguments);
		}

		private void finishInferenceRule() {
			inferenceRules.add(new InferenceRule(given, infer));
		}

		private void finishRewriteRule() {
			rewriteRule.addRightHandSide(rightHandSides);
		}

		private void finishProofRulesBlock() {
			block.addInferenceRules(inferenceRules);
			block.addMetaVariables(metaVars);
			block.addRewriteRules(rewriteRules);
		}

		private void finishRecursiveDefinition() {
			((RecursiveOperatorDefinition) definition)
					.addCases(recursiveDefinitions);
		}

		private void finishAxiomaticOperator() {
			axiomaticOperator.addArguments(opArgs);

			typeEnv.add(axiomaticOperator.getFormulaExtension());
		}

		private void finishOperator() {
			operator.setDefinition(definition);
			operator.addArguments(opArgs);

			typeEnv.add(operator.getFormulaExtension());

			// Recursive definition cases have to be parsed after the formula
			// extension for the operator is already defined
			if (recursiveDefinitions != null) {
				for (RecursiveDefinitionCase def : recursiveDefinitions) {
					def.parseCase(typeEnv);
				}
				recursiveDefinitions = null;
			}
		}

		private void finishDataType() {
			dataType.addConstructors(constructors);
			dataType.addTypeArguments(typeArguments);
			Set<IFormulaExtension> newExts = dataType
					.getFormulaExtensions(FormulaFactory.getInstance(typeEnv));
			typeEnv.addAll(newExts);

			dataType.parseElements(typeEnv);
		}

		@Override
		public void endDocument() throws SAXException {
			theory.addDataTypes(dataTypes);
			theory.addImported(imported);
			theory.addOperators(operators);
			theory.addAxiomaticDefintionsBlocks(axiomaticDefinitionsBlocks);
			theory.addProofRules(proofRules);
			theory.addTheorems(theorems);
			theory.addTypeParameters(typeParameters);
		}
	}

}
