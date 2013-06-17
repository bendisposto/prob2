package de.prob.worksheet;

public class Box {

	protected String id;
	protected String content;
	private IRenderer outputRenderer;
	private IRenderer errorRenderer;
	private IEvaluator evaluator;
	private EBoxTypes type;

	public Box(EBoxTypes type, String id, String content, IRenderer regular,
			IRenderer error, IEvaluator evaluator) {
		this.type = type;
		this.id = id;
		this.content = content;
		this.outputRenderer = regular;
		this.errorRenderer = error;
		this.evaluator = evaluator;
	}

	public String render(WorkSheet context) {
		Object result = evaluator.evaluate(context, content);
		if (result instanceof Throwable) {
			return errorRenderer.render(context, result);
		}
		return outputRenderer.render(context, result);
	}

	public String getText() {
		return content;
	}

	public EBoxTypes getType() {
		return type;
	}

	public void setText(String content) {
		this.content = content;
	}

}
