package de.prob.bmotion

import com.google.gson.Gson

class TransformerObserver implements IBMotionObserver {

    def Gson g = new Gson()

    def List<IBMotionTransformer> transformers = []

    @Override
    def apply(BMotion bms) {
        String json = g.toJson(transformers.collectMany { it.update(bms) })
        bms.submit([cmd:"bms.applyTransformers", transformers:json])
    }

}
