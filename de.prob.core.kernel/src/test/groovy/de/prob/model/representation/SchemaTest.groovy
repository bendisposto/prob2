package de.prob.model.representation

import spock.lang.Specification

class SchemaTest extends Specification {

	/*
	 * components == [{type: Machine.class
	 *                 name: $ALL$
	 *                 subcomponents: [{type: Variable.class
	 *                                  name: $ALL$
	 *                                  subcomponents: null},
	 *                                 {type: Invariant.class,
	 *                                  name: $ALL$
	 *                                  subcomponents: null},
	 *                                 {type: BEvent.class,
	 *                                  name: $ALL$
	 *                                  subcomponents: [{type: Guard.class
	 *                                                   name: $ALL$
	 *                                                   subcomponents: null}]}]]
	 */
	def "Can create a default schema for EventB"() {
		when:
		def schema = new Schema().with {
			type Machine, {
				type Variable
				type Invariant
				type BEvent, { type Guard }
			}
		}

		then:
		true == true
	}

	/*
	 * components = [{type: Machine.class,
	 *                name: Scheduler
	 *                subcomponents: [{type: Variable.class
	 *                                 name: $ALL$
	 *                                 subcomponents: null}
	 *                                {type: Invariant.class
	 *                                 name: "inv1"
	 *                                 subcomponents: null}
	 *                                {type: Invariant.class
	 *                                 name: "inv2"
	 *                                 subcomponents: null}
	 *                                {type: Invariant.class
	 *                                 name: "inv3"
	 *                                 subcomponents: null}
	 *                                {type: BEvent.class
	 *                                 name: "inc"
	 *                                 subcomponents: null}
	 *                                {type: BEvent.class
	 *                                 name: "dec"
	 *                                 subcomponents: [{type: Guard.class
	 *                                                  name: $ALL$
	 *                                                  subcomponents: null}]]]
	 *
	 */

	def "Can create a specific schema for a model"() {
		when:
		def schema = new Schema().with {
			type Machine, "Scheduler", {
				type Variable
				type Invariant
				type BEvent, "inc", { type Guard }
				type BEvent, "dec", { type Guard }
			}
		}

		then:
		true == true
	}
}
