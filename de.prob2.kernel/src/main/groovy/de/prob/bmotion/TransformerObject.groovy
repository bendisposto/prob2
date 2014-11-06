package de.prob.bmotion

import com.google.gson.Gson
import groovy.transform.TupleConstructor

@TupleConstructor
class TransformerObject {

    def String selector
    def attributes = [:]
    def styles = [:]
    def String content

    def TransformerObject(String selector) {
        this.selector = selector
    }

    def TransformerObject set(String name, Object value) {
        (name == "content" || name == "text") ? content = value : attributes.put(name, value)
        this
    }

    def TransformerObject attr(String name, Object value) {
        set(name, value)
    }

    def TransformerObject style(String name, Object value) {
        styles.put(name, value)
        this
    }

    @Override
    public String toString() {
        Gson g = new Gson();
        return g.toJson(this);
    }

}
