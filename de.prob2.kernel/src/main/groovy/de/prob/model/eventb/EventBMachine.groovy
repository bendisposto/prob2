package de.prob.model.eventb;

import com.github.krukow.clj_lang.PersistentHashMap

import de.prob.model.representation.AbstractElement
import de.prob.model.representation.BEvent
import de.prob.model.representation.Invariant
import de.prob.model.representation.Machine
import de.prob.model.representation.ModelElementList
import de.prob.model.representation.Variable

public class EventBMachine extends Machine {

	public EventBMachine(final String name) {
		super(name, PersistentHashMap.emptyMap())
	}

	private EventBMachine(final String name, children) {
		super(name, children)
	}

	def EventBMachine set(Class<? extends AbstractElement> clazz, ModelElementList<? extends AbstractElement> elements) {
		new EventBMachine(name, children.assoc(clazz, elements))
	}

	def <T extends AbstractElement, S extends T> EventBMachine addTo(Class<T> clazz, S element) {
		ModelElementList<T> list = getChildrenOfType(clazz)
		return new EventBMachine(name, assoc(clazz, list.addElement(element)))
	}

	def <T extends AbstractElement, S extends T> EventBMachine removeFrom(Class<T> clazz, S element) {
		ModelElementList<T> list = getChildrenOfType(clazz)
		return new EventBMachine(name, assoc(clazz, list.removeElement(element)))
	}

	def <T extends AbstractElement, S extends T> EventBMachine replaceIn(Class<T> clazz, S oldElement, S newElement) {
		ModelElementList<T> list = getChildrenOfType(clazz)
		return new EventBMachine(name, assoc(clazz, list.replaceElement(oldElement, newElement)))
	}

	def ModelElementList<EventBMachine> getRefines() {
		getChildrenOfType(Machine)
	}

	def ModelElementList<Context> getSees() {
		getChildrenOfType(Context)
	}

	def ModelElementList<EventBVariable> getVariables() {
		getChildrenOfType(Variable)
	}

	def ModelElementList<EventBInvariant> getInvariants() {
		getChildrenOfType(Invariant)
	}

	def Variant getVariant() {
		def variant = getChildrenOfType(Variant)
		variant ? variant[0] : null
	}

	def ModelElementList<ProofObligation> getProofs() {
		getChildrenOfType(ProofObligation)
	}

	def ModelElementList<Event> getEvents() {
		getChildrenOfType(BEvent)
	}

	public Event getEvent(String name) {
		getEvents().getElement(name)
	}
}
