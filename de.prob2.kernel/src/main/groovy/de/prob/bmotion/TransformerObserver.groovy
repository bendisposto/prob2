package de.prob.bmotion

import com.google.gson.Gson
import groovy.transform.TupleConstructor

@TupleConstructor
class TransformerObserver extends BMotionObserver implements BMotionTransformer {

    def _selector

    def _attributes = [:]

    def _styles = [:]

    def _content

    private final Gson g = new Gson()

    def static TransformerObserver make(Closure cls) {
        new TransformerObserver().with cls
    }

    def TransformerObserver selector(selector) {
        this._selector = selector
        this
    }

    def TransformerObserver set(String name, Object value) {
        (name == "content" || name == "text") ? _content = value : _attributes.put(name, value)
        this
    }

    def TransformerObserver attr(String name, Object value) {
        set(name, value)
    }

    def TransformerObserver style(String name, Object value) {
        _styles.put(name, value)
        this
    }

    def List<TransformerObject> update(BMotion bms) {
        def t = new TransformerObject((_selector instanceof Closure) ? _selector() : _selector)
        t.attributes = _attributes.collectEntries { kv ->
            (kv.value instanceof Closure) ? [kv.key, kv.value()] : [kv.key, kv.value
            ]
        }
        t.styles = _styles.collectEntries { kv ->
            (kv.value instanceof Closure) ? [kv.key, kv.value()] : [kv.key, kv.value
            ]
        }
        t.content = (_content instanceof Closure) ? _content() : _content
        [t]
    }

    @Override
    def apply(BMotion bms) {
        bms.submit([cmd: "bms.applyTransformers", transformers: g.toJson(update(bms))])
    }

}