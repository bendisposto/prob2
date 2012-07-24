package de.prob.statespace

class SyncHistory {
	def List<History> histories
	def List<String> syncedOps

	def SyncHistory head
	def SyncHistory current
	def SyncHistory prev

	def SyncHistory(histories,syncedOps) {
		this.histories = histories
		this.syncedOps = syncedOps
		this.head = this
		this.current = head
		this.prev = null
	}

	def SyncHistory(histories,prev,syncedOps) {
		this.histories = histories
		this.head = this
		this.current = head
		this.prev = prev
		this.syncedOps = syncedOps
	}
	
	def SyncHistory(histories,head,current,prev,syncedOps) {
		this.histories = histories
		this.head = head
		this.current = current
		this.prev = prev
		this.syncedOps = syncedOps
	}

	def SyncHistory add(String syncedOp, List<String> params) {
		if(!syncedOps.contains(syncedOp)) {
			throw new IllegalArgumentException("The given operation has not been specified as a syncronized operation")
		}
		def map = new HashMap<History, String>()
		histories.each { history ->
			def op = history.getOp(syncedOp,params)
			if(op==null) {
				throw new IllegalArgumentException("Operation cannot be synced across the given histories")
			}
			map.put(history, op)
		}
		def newHistories = []
			histories.each { history ->
				newHistories << history.add(map.get(history))
			}
		return new SyncHistory(newHistories,this,syncedOps)
	}

	def SyncHistory add(String op, List<String> params, int index) {
		if(syncedOps.contains(op)) {
			return add(op,params)
		}
		def history = histories.get(index)
		def operation = history.getOp(op,params)
		history = history.add(operation)
		def newHistories = []
		histories.each { newHistories << it }
		newHistories.set(index, history)
		return new SyncHistory(newHistories,this,syncedOps)
	}
	
	def SyncHistory back() {
		if(prev != null)
			return new SyncHistory(prev.histories,head,prev,prev.prev,syncedOps)
		return this
	}
	
	def SyncHistory forward() {
		if(current != head) {
			SyncHistory p = head
			while( p.prev != current ) {
				p = p.prev
			}
			return new SyncHistory(p.histories,head,p,p.prev,syncedOps)
		}
		return this
	}
	
	def SyncHistory addOp(String op) {
		def newSyncedOps = []
		syncedOps.each { newSyncedOps << it }
		newSyncedOps.add(op)
		return new SyncHistory(histories,head,current,prev,newSyncedOps)
	}
	
	def String toString() {
		def sb = new StringBuilder()
		histories.each { history ->
			sb.append("${histories.indexOf(history)}: ${history.toString()}\n")
		}
		sb.toString()
	}
}
