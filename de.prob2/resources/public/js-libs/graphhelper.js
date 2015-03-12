/*
In clojure script, can be used something like:
(def g (.createGraph js/graph))
(defn nodefn [name] (clj->js {:label name :width 100 :height 100}))
(.addNode js/graph g "me" (nodefn "Me"))
(.addNode js/graph g "you" (nodefn "You"))
(.addEdge js/graph g "me" "you" "us")
(.render js/graph g)
(.nodes js/graph (.render js/graph g))
(.edges js/graph (.render js/graph g))

if     
    {% script "js-libs/dagre.min.js" %}
    {% script "js-libs/graphhelper.js" %} 
is included in the index template
*/

graph = {
	createGraph: function() {
		var g = new dagre.graphlib.Graph()
		g.setGraph({})
		g.setDefaultEdgeLabel
		return g
	},

	addNode: function(g, name, traits) {
		g.setNode(name, {label: traits.label, width: traits.width, height: traits.height})
		return g
	},

	addEdge: function(g, from, to, l) {
		console.log(l)
		g.setEdge(from, to, {label: l})
		return g
	},

	render: function(g) {
		dagre.layout(g)
		return g
	},

	nodes: function(g) {
		var d = []
		g.nodes().forEach(function(v) {
			d.push(g.node(v))
		})
		return d
	},

	edges: function(g) {
		var d = []
		g.edges().forEach(function(v) {
			d.push(g.edge(v))
		})
		return d
	}
}