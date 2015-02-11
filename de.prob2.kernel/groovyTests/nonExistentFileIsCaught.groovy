import de.prob.animator.domainobjects.*
import de.prob.statespace.*

caught = false
try {
	m = api.b_load("blah.mch")
} catch(FileNotFoundException e) {
	caught = true
}
assert caught

caught = false
try {
	m = api.b_load("blah.ref")
} catch(FileNotFoundException e) {
	caught = true
}
assert caught

caught = false
try {
	m = api.eventb_load("blub.buc")
} catch(FileNotFoundException e) {
	caught = true
}
assert caught

caught = false
try {
	m = api.eventb_load("blub.bum")
} catch(FileNotFoundException e) {
	caught = true
}
assert caught

caught = false
try {
	m = api.eventb_load("blub.bcc")
} catch(FileNotFoundException e) {
	caught = true
}
assert caught

caught = false
try {
	m = api.eventb_load("blub.bcm")
} catch(FileNotFoundException e) {
	caught = true
}
assert caught

caught = false
try {
	m = api.csp_load("blub.csp")
} catch(FileNotFoundException e) {
	caught = true
}
assert caught

caught = false
try {
	m = api.tla_load("blub.tla")
} catch(FileNotFoundException e) {
	caught = true
}
assert caught

"a FileNotFoundException is thrown if the specified model does not exist"