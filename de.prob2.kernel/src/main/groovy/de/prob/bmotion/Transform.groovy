package de.prob.bmotion

import com.google.gson.Gson
import groovy.transform.TupleConstructor

@TupleConstructor
class Transform {

    def String selector
    def attributes = [:]
    def styles = [:]
    def String content

    def Transform set(String name, Object value) {
        (name == "content" || name == "text") ? content = value : attributes.put(name,value)
        this
    }

    def Transform attr(String name, Object value) {
        set(name, value)
    }

    def Transform style(String name, Object value) {
        styles.put(name, value)
        this
    }

    @Override
    public String toString() {
        Gson g = new Gson();
        return g.toJson(this);
    }

}
