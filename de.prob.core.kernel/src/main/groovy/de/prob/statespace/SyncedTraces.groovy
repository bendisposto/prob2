package de.prob.statespace




class SyncedTraces {
	def List<Trace> traces
	def List<String> syncedOps

	def SyncedTraces head
	def SyncedTraces current
	def SyncedTraces prev

	def SyncedTraces(List<StateSpace> statespaces, syncedOps) {
		traces = []
		statespaces.each { s ->
			traces << new Trace(s)
		}
		this.syncedOps = syncedOps
		this.head = this
		this.current = head
		this.prev = null
	}

	def SyncedTraces(traces,prev,syncedOps) {
		this.traces = traces
		this.head = this
		this.current = head
		this.prev = prev
		this.syncedOps = syncedOps
	}

	def SyncedTraces(traces,head,current,prev,syncedOps) {
		this.traces = traces
		this.head = head
		this.current = current
		this.prev = prev
		this.syncedOps = syncedOps
	}

	def SyncedTraces add(String syncedOp, List<String> params) {
		if(!syncedOps.contains(syncedOp)) {
			throw new IllegalArgumentException("The given operation has not been specified as a syncronized operation")
		}
		def map = new HashMap<Trace, String>()
		traces.each { trace ->
			def op = trace.getOp(syncedOp,params)
			if(op==null) {
				throw new IllegalArgumentException("Operation cannot be synced across the given traces")
			}
			map.put(trace, op)
		}
		def newTraces = []
		traces.each { trace ->
			newTraces << trace.add(map.get(trace))
		}
		return new SyncedTraces(newTraces,this,syncedOps)
	}

	def SyncedTraces add(String op, List<String> params, int index) {
		if(syncedOps.contains(op)) {
			return add(op,params)
		}
		def trace = traces.get(index)
		def operation = trace.getOp(op,params)
		trace = trace.add(operation)
		def newTraces = []
		traces.each { newTraces << it }
		newTraces.set(index, trace)
		return new SyncedTraces(newTraces,this,syncedOps)
	}

	def SyncedTraces add(String opId, int index) {
		def trace = traces.get(index)
		def op = trace.stateSpace.getOp(opId)
		if(syncedOps.contains(op.getName())) {
			return add(op.getName(),op.getParams())
		}
		trace = trace.add(opId)
		def newTraces = []
		traces.each { newTraces << it }
		newTraces.set(index, trace)
		return new SyncedTraces(newTraces,this,syncedOps)
	}

	def SyncedTraces add(int opId, int index) {
		return add(String.valueOf(opId),index)
	}

	def SyncedTraces back() {
		if(prev != null)
			return new SyncedTraces(prev.traces,head,prev,prev.prev,syncedOps)
		return this
	}

	def SyncedTraces forward() {
		if(current != head) {
			SyncedTraces p = head
			while( p.prev != current ) {
				p = p.prev
			}
			return new SyncedTraces(p.traces,head,p,p.prev,syncedOps)
		}
		return this
	}

	def SyncedTraces addOp(String op) {
		def newSyncedOps = []
		syncedOps.each { newSyncedOps << it }
		newSyncedOps.add(op)
		return new SyncedTraces(traces,head,current,prev,newSyncedOps)
	}

	def String toString() {
		def sb = new StringBuilder()

		traces.each { trace ->
			sb.append("${traces.indexOf(trace)}: ${trace.getRep()}\n")
		}

		def h = traces.get(0)
		def currentOpsOnH = h.stateSpace.getOutEdges(h.current.getCurrentState())
		def copy = new HashSet<OpInfo>(currentOpsOnH)

		currentOpsOnH.each { op ->
			if(syncedOps.contains(op.getName())) {
				traces.each { trace ->
					def op2 = trace.getOp(op.getName(),op.getParams())
					if(op2==null) {
						copy.remove(op)
					}
				}
			} else {
				copy.remove(op)
			}
		}

		sb.append("Operations:\n")
		sb.append("synced: ${copy}\n")
		traces.each { trace ->
			sb.append("${traces.indexOf(trace)}: ")
			def o = trace.stateSpace.getOutEdges(trace.current.getCurrentState())
			def list = []
			o.each {
				if(!syncedOps.contains(it.getName())) {
					list << "${it.getId()}: ${it.getRep(s.getModel())}"
				}
			}
			sb.append(list)
			sb.append("\n")
		}
		return sb.toString()
	}
}
