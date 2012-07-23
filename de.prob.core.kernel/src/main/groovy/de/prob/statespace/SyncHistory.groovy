package de.prob.statespace

class SyncHistory {
	def List<History> histories
	def List<String> syncedOps

	def SyncHistory head
	def SyncHistory current
	def SyncHistory prev

	def SyncHistory(histories) {
		this.histories = histories
		this.head = this
		this.current = head
		this.prev = null
	}


	def SyncHistory(histories,prev) {
		this.histories = histories
		this.head = this
		this.current = head
		this.prev = prev
	}
	
	def SyncHistory(histories,head,current,prev) {
		this.histories = histories
		this.head = head
		this.current = current
		this.prev = prev
	}

	def SyncHistory add(String syncedOp, List<String> params) {
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
		return new SyncHistory(newHistories,this)
	}

	def SyncHistory add(String op, int index) {
		def history = histories.get(index)
		history = history.add(op)
		def newHistories = []
		histories.each { newHistories << it }
		newHistories.set(index, history)
		return new SyncHistory(newHistories,this)
	}
	
	def SyncHistory back() {
		if(prev != null)
			return new SyncHistory(prev.histories,head,prev,prev.prev)
		return this
	}
	
	def SyncHistory forward() {
		if(current != head) {
			SyncHistory p = head
			while( p.prev != current ) {
				p = p.prev
			}
			return new SyncHistory(p.histories,head,p,p.prev)
		}
		return this
	}
}
