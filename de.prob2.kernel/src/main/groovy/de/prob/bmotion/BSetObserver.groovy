package de.prob.bmotion
//TODO: Check if result of expression is an enumerated set
class BSetObserver extends TransformerObserver {

    def _expression
    def _convert = { "#" + it }
    def _resolve = { it != null ? it.value.replace("{", "").replace("}", "").replaceAll(" ", "").tokenize(",") : [] }

    def BSetObserver(String expression) {
        this._expression = expression
    }

    def static BSetObserver make(Closure cls) {
        new BSetObserver().with cls
    }

    def BSetObserver expression(expression) {
        this._expression = expression
        this
    }

    def BSetObserver convert(Closure cls) {
        this._convert = cls
        this
    }

    def BSetObserver resolve(Closure cls) {
        this._resolve = cls
        this
    }

    def List<TransformerObject> update(BMotion bms) {
        def a = _resolve(bms.eval((_expression instanceof Closure) ? _expression() : _expression))
        def b = a.collect { _convert(it) }
        _selector = b == [] ? "" : b.join(",")
        super.update(bms)
    }

}