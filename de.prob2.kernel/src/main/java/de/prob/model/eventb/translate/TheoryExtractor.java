package de.prob.model.eventb.translate;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import de.prob.animator.domainobjects.EventB;
import de.prob.animator.domainobjects.FormulaExpand;
import de.prob.model.eventb.EventBAxiom;
import de.prob.model.eventb.theory.AxiomaticDefinitionBlock;
import de.prob.model.eventb.theory.DataType;
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
import de.prob.util.Tuple2;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.extension.IFormulaExtension;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class TheoryExtractor extends DefaultHandler {

	private final Logger logger = LoggerFactory.getLogger(TheoryExtractor.class);

	private Theory theory;
	private ModelElementList<Theory> imported = new ModelElementList<>();
	private ModelElementList<Type> typeParameters = new ModelElementList<>();
	private ModelElementList<DataType> dataTypes = new ModelElementList<>();
	private ModelElementList<Operator> operators = new ModelElementList<>();
	private ModelElementList<AxiomaticDefinitionBlock> axiomaticDefinitionsBlocks = new ModelElementList<>();
	private ModelElementList<EventBAxiom> theorems = new ModelElementList<>();
	private ModelElementList<ProofRulesBlock> proofRules = new ModelElementList<>();

	// For adding DataType
	private String dataTypeName;
	private String currentConstructor;
	private Map<String, List<Tuple2<String, String>>> constructors;
	private List<String> types;

	private ModelElementList<Type> typeArguments; // Also used for axiomatic
	// definition blocks

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

	private Set<IFormulaExtension> typeEnv;

	private Map<String, Theory> theoryMap;

	private String project;

	private String name;

	private String workspacePath;
	private ModelElementList<Theory> theories = new ModelElementList<>();

	public TheoryExtractor(final String workspacePath, String project,
			String name, Map<String, Theory> theoryMap) {
		this.workspacePath = workspacePath;
		this.project = project;
		this.name = name;
		this.theoryMap = theoryMap;
		Collection<OperatorMapping> mappings = new ArrayList<>();
		try {
			String mappingFileName = workspacePath + File.separator + project
					+ File.separator + name + ".ptm";
			mappings = TheoryMappingParser.parseTheoryMapping(name,
					mappingFileName);
		} catch (FileNotFoundException e) {
			logger.warn("No .ptm file found for Theory {}. This means that ProB has no information on how to interpret this theory.", name, e);
		} catch (IOException | TheoryMappingException e) {
			logger.error("Error extracting theory", e);
		}
		theory = new Theory(name, project, mappings);
		typeEnv = new HashSet<>();

	}

	public Theory getTheory() {
		return theory;
	}

	@Override
	public void startElement(final String uri, final String localName,
			final String qName, final Attributes attributes)
					throws SAXException {
		switch (qName) {
			case "org.eventb.theory.core.scTypeParameter":
				addTypeParameter(attributes);
				break;
			case "org.eventb.theory.core.useTheory":
				addUsedTheory(attributes);
				break;
			case "org.eventb.theory.core.scDatatypeDefinition":
				beginAddingDataType(attributes);
				break;
			case "org.eventb.theory.core.scTypeArgument":
				addTypeArgument(attributes);
				break;
			case "org.eventb.theory.core.scDatatypeConstructor":
				beginAddingDataTypeConstructor(attributes);
				break;
			case "org.eventb.theory.core.scConstructorArgument":
				addDestructor(attributes);
				break;
			case "org.eventb.theory.core.scNewOperatorDefinition":
				beginAddingOperator(attributes);
				break;
			case "org.eventb.theory.core.scDirectOperatorDefinition":
				addDirectDefinition(attributes);
				break;
			case "org.eventb.theory.core.scRecursiveOperatorDefinition":
				beginRecursiveOpDef(attributes);
				break;
			case "org.eventb.theory.core.scRecursiveDefinitionCase":
				addRecursiveDefinitionCase(attributes);
				break;
			case "org.eventb.theory.core.scOperatorArgument":
				addOperatorArgument(attributes);
				break;
			case "org.eventb.theory.core.scAxiomaticDefinitionsBlock":
				addAxiomaticDefinitionBlock(attributes);
				break;
			case "org.eventb.theory.core.scAxiomaticOperatorDefinition":
				beginAddingAxiomaticOperator(attributes);
				break;
			case "org.eventb.theory.core.scAxiomaticDefinitionAxiom":
				addDefinitionAxiom(attributes);
				break;
			case "org.eventb.theory.core.scAxiomaticTypeDefinition":
				addTypeParameter(attributes);
				break;
			case "org.eventb.theory.core.scTheorem":
				addTheorem(attributes);
				break;
			case "org.eventb.theory.core.scProofRulesBlock":
				beginProofRulesBlock(attributes);
				break;
			case "org.eventb.theory.core.scMetavariable":
				addMetaVariable(attributes);
				break;
			case "org.eventb.theory.core.scRewriteRule":
				beginRewriteRule(attributes);
				break;
			case "org.eventb.theory.core.scRewriteRuleRHS":
				addRightHandSide(attributes);
				break;
			case "org.eventb.theory.core.scInferenceRule":
				beginInferenceRule(attributes);
				break;
			case "org.eventb.theory.core.scInfer":
				addInfer(attributes);
				break;
			case "org.eventb.theory.core.scGiven":
				addGiven(attributes);
				break;
		}
	}

	private void addGiven(final Attributes attributes) {
		String predicate = attributes.getValue("org.eventb.core.predicate");
		given.add(new EventB(predicate, typeEnv, FormulaExpand.EXPAND));
	}

	private void addInfer(final Attributes attributes) {
		String predicate = attributes.getValue("org.eventb.core.predicate");
		infer = new EventB(predicate, typeEnv, FormulaExpand.EXPAND);
	}

	private void beginInferenceRule(final Attributes attributes) {
		given = new ArrayList<>();
	}

	private void addRightHandSide(final Attributes attributes) {
		String name = attributes.getValue("org.eventb.core.label");
		String predicate = attributes.getValue("org.eventb.core.predicate");
		String formula = attributes.getValue("org.eventb.theory.core.formula");
		rightHandSides = rightHandSides.addElement(new RewriteRuleRHS(name,
				predicate, formula, typeEnv));
	}

	private void beginRewriteRule(final Attributes attributes) {
		String label = attributes.getValue("org.eventb.core.label");
		String applicability = attributes
				.getValue("org.eventb.theory.core.applicability");
		boolean complete = "true".equals(attributes
				.getValue("org.eventb.theory.core.complete"));
		String desc = attributes.getValue("org.eventb.theory.core.desc");
		String formula = attributes.getValue("org.eventb.theory.core.formula");
		rewriteRule = new RewriteRule(label, applicability, complete, desc,
				formula, typeEnv);

		rightHandSides = new ModelElementList<>();
		rewriteRules = rewriteRules.addElement(rewriteRule);
	}

	private void addMetaVariable(final Attributes attributes) {
		String name = attributes.getValue("name");
		String type = attributes.getValue("org.eventb.core.type");
		metaVars = metaVars.addElement(new MetaVariable(name, type, typeEnv));
	}

	private void beginProofRulesBlock(final Attributes attributes) {
		String name = attributes.getValue("org.eventb.core.label");
		if (name == null) {
			name = attributes.getValue("name");
		}
		block = new ProofRulesBlock(name);

		metaVars = new ModelElementList<>();
		rewriteRules = new ModelElementList<>();
		inferenceRules = new ModelElementList<>();

		proofRules = proofRules.addElement(block);
	}

	private void addTheorem(final Attributes attributes) {
		String label = attributes.getValue("org.eventb.core.label");
		String predicate = attributes.getValue("org.eventb.core.predicate");

		theorems = theorems.addElement(new EventBAxiom(label, predicate, true,
				typeEnv));
	}

	private void addAxiomaticDefinitionBlock(final Attributes attributes) {
		String name = attributes.getValue("org.eventb.core.label");
		axiomaticDefinitionBlock = new AxiomaticDefinitionBlock(name);
		typeArguments = new ModelElementList<>();
		axiomaticOperators = new ModelElementList<>();
		definitionAxioms = new ModelElementList<>();

		axiomaticDefinitionsBlocks = axiomaticDefinitionsBlocks
				.addElement(axiomaticDefinitionBlock);
	}

	private void beginAddingAxiomaticOperator(final Attributes attributes) {
		axiomaticOperator = createOperator(attributes);

		opArgs = new ModelElementList<>();
	}

	private void addDefinitionAxiom(final Attributes attributes) {
		String label = attributes.getValue("org.eventb.core.label");
		String predicate = attributes.getValue("org.eventb.core.predicate");

		definitionAxioms = definitionAxioms.addElement(new EventBAxiom(label,
				predicate, false, typeEnv));
	}

	private void addOperatorArgument(final Attributes attributes) {
		String identifier = attributes.getValue("name");
		String type = attributes.getValue("org.eventb.core.type");
		opArgs = opArgs.addElement(new OperatorArgument(identifier, type,
				typeEnv));
	}

	private void addDirectDefinition(final Attributes attributes) {
		String formula = attributes.getValue("org.eventb.theory.core.formula");
		definition = new DirectDefinition(formula, typeEnv);
	}

	private void addRecursiveDefinitionCase(final Attributes attributes) {
		String expression = attributes.getValue("org.eventb.core.expression");
		String formula = attributes.getValue("org.eventb.theory.core.formula");
		recursiveDefinitions = recursiveDefinitions
				.addElement(new RecursiveDefinitionCase(expression, formula));
	}

	private void beginRecursiveOpDef(final Attributes attributes) {
		String indArg = attributes
				.getValue("org.eventb.theory.core.inductiveArgument");
		definition = new RecursiveOperatorDefinition(indArg, typeEnv);

		recursiveDefinitions = new ModelElementList<>();
	}

	private void beginAddingOperator(final Attributes attributes) {
		operator = createOperator(attributes);

		opArgs = new ModelElementList<>();
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
		String groupId = attributes.getValue("org.eventb.theory.core.groupID");
		String predicate = attributes.getValue("org.eventb.core.predicate");
		String type = attributes.getValue("org.eventb.theory.core.type");
		String wd = attributes.getValue("org.eventb.theory.core.wd");
		return new Operator(theory.getName(), label, associative, commutative,
				formulaType, notationType, groupId, type, wd, predicate,
				typeEnv);
	}

	private void addDestructor(final Attributes attributes) {
		String name = attributes.getValue("name");
		String type = attributes.getValue("org.eventb.core.type");

		constructors.get(currentConstructor).add(
			new Tuple2<>(name, type));
	}

	private void beginAddingDataTypeConstructor(final Attributes attributes) {
		String name = attributes.getValue("name");

		currentConstructor = name;
		constructors.put(currentConstructor,
			new ArrayList<>());
	}

	private void beginAddingDataType(final Attributes attributes) {
		String name = attributes.getValue("name");
		dataTypeName = name;
		constructors = new HashMap<>();
		types = new ArrayList<>();

	}

	private void addUsedTheory(final Attributes attributes) throws SAXException {
		String target = attributes.getValue("org.eventb.core.scTarget");
		String path = target.substring(0, target.indexOf('|'));
		if (theoryMap.containsKey(path)) {
			imported = imported.addElement(theoryMap.get(path));
		} else {
			try {
				String dir = path.substring(path.indexOf('/') + 1,
						path.lastIndexOf('/'));
				String name = path.substring(path.lastIndexOf('/') + 1,
						path.lastIndexOf('.'));
				SAXParserFactory parserFactory = SAXParserFactory.newInstance();
				parserFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
				SAXParser saxParser = parserFactory.newSAXParser();

				TheoryExtractor extractor = new TheoryExtractor(workspacePath,
						dir, name, theoryMap);
				saxParser.parse(new File(workspacePath + path), extractor);
				theories = theories.addElement(extractor.getTheory());
				typeEnv.addAll(extractor.getTypeEnv());
			} catch (IOException | ParserConfigurationException e) {
				logger.error("Error extracting theory", e);
			}
		}
	}

	private void addTypeParameter(final Attributes attributes) {
		String name = attributes.getValue("name");
		Type p = new Type(name, typeEnv);
		typeParameters = typeParameters.addElement(p);
	}

	private void addTypeArgument(final Attributes attributes) {
		String name = attributes.getValue("name");
		types.add(name);
	}

	@Override
	public void endElement(final String uri, final String localName,
			final String qName) {
		switch (qName) {
			case "org.eventb.theory.core.scDatatypeDefinition":
				finishDataType();
				break;
			case "org.eventb.theory.core.scNewOperatorDefinition":
				finishOperator();
				break;
			case "org.eventb.theory.core.scRecursiveDefinitionCase":
				finishRecursiveDefinition();
				break;
			case "org.eventb.theory.core.scAxiomaticOperatorDefinition":
				finishAxiomaticOperator();
				break;
			case "org.eventb.theory.core.scAxiomaticDefinitionsBlock":
				finishAxiomaticDefinitionBlock();
				break;
			case "org.eventb.theory.core.scProofRulesBlock":
				finishProofRulesBlock();
				break;
			case "org.eventb.theory.core.scRewriteRule":
				finishRewriteRule();
				break;
			case "org.eventb.theory.core.scInferenceRule":
				finishInferenceRule();
				break;
		}
	}

	private void finishAxiomaticDefinitionBlock() {
		axiomaticDefinitionBlock = axiomaticDefinitionBlock.set(
				EventBAxiom.class, definitionAxioms);
		axiomaticDefinitionBlock = axiomaticDefinitionBlock.set(Operator.class,
				axiomaticOperators);
		axiomaticDefinitionBlock = axiomaticDefinitionBlock.set(Type.class,
				typeArguments);
	}

	private void finishInferenceRule() {
		inferenceRules = inferenceRules.addElement(new InferenceRule(given,
				infer));
	}

	private void finishRewriteRule() {
		rewriteRule = rewriteRule.addRightHandSide(rightHandSides);
	}

	private void finishProofRulesBlock() {
		block = block.set(InferenceRule.class, inferenceRules);
		block = block.set(MetaVariable.class, metaVars);
		block = block.set(RewriteRule.class, rewriteRules);
	}

	private void finishRecursiveDefinition() {
		definition = ((RecursiveOperatorDefinition) definition)
				.addCases(recursiveDefinitions);
	}

	private void finishAxiomaticOperator() {
		axiomaticOperator = axiomaticOperator.addArguments(opArgs);
		axiomaticOperators = axiomaticOperators.addElement(axiomaticOperator);

		typeEnv.add(axiomaticOperator.getFormulaExtension());
	}

	private void finishOperator() {
		operator = operator.setDefinition(definition);
		operator = operator.addArguments(opArgs);

		typeEnv.add(operator.getFormulaExtension());

		operators = operators.addElement(operator);

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
		DataType dataType = new DataType(dataTypeName, constructors, types);

		Set<IFormulaExtension> newExts = dataType
				.getFormulaExtensions(FormulaFactory.getInstance(typeEnv));
		dataTypes = dataTypes.addElement(dataType);
		typeEnv.addAll(newExts);
	}

	@Override
	public void endDocument() {
		theory = theory.set(DataType.class, dataTypes);
		theory = theory.set(Theory.class, imported);
		theory = theory.set(Operator.class, operators);
		theory = theory.set(AxiomaticDefinitionBlock.class,
				axiomaticDefinitionsBlocks);
		theory = theory.set(ProofRulesBlock.class, proofRules);
		theory = theory.set(EventBAxiom.class, theorems);
		theory = theory.set(Type.class, typeParameters);

		theoryMap.put(project + File.separator + name, theory);
		theory = theory.setTypeEnvironment(typeEnv);
		theories = theories.addElement(theory);
	}

	public ModelElementList<Theory> getTheories() {
		return theories;
	}

	public Set<IFormulaExtension> getTypeEnv() {
		return typeEnv;
	}
}
