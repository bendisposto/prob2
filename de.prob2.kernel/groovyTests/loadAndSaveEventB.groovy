import de.prob.model.eventb.*
import de.prob.model.eventb.proof.*
import de.prob.model.eventb.translate.*

/*
 * Tests loading of EventB to make sure that all components are there. 
 */

s = api.eventb_load(dir+File.separator+"Lift"+File.separator+"lift0.bcm")
assert s != null

api.eventb_save(s, dir+File.separator+"Lift"+File.separator+"lift0.eventb")

String fileContents = new File(dir+File.separator+"Lift"+File.separator+"lift0.eventb").text.trim()
assert fileContents.equals("package(load_event_b_project([event_b_model(none,lift0,[sees(none,[levels]),variables(none,[identifier(none,level)]),invariant(none,[member(rodinpos(lift0,inv1,[]),identifier(none,level),identifier(none,levels))]),theorems(none,[]),events(none,[event(rodinpos(lift0,'INITIALISATION',[]),'INITIALISATION',ordinary(none),[],[],[],[],[assign(rodinpos(lift0,act1,[]),[identifier(none,level)],[identifier(none,'L0')])],[]),event(rodinpos(lift0,up,[]),up,ordinary(none),[],[],[member(rodinpos(lift0,grd1,[]),identifier(none,level),set_extension(none,[identifier(none,'L0'),identifier(none,'L1'),identifier(none,'L2')]))],[],[assign(rodinpos(lift0,act1,[]),[identifier(none,level)],[function(none,identifier(none,up),[identifier(none,level)])])],[]),event(rodinpos(lift0,down,[]),down,ordinary(none),[],[],[member(rodinpos(lift0,grd1,[]),identifier(none,level),set_extension(none,[identifier(none,'L1'),identifier(none,'L2'),identifier(none,'L3')]))],[],[assign(rodinpos(lift0,act1,[]),[identifier(none,level)],[function(none,identifier(none,down),[identifier(none,level)])])],[]),event(rodinpos(lift0,randomCrazyJump,[]),randomCrazyJump,ordinary(none),[],[identifier(rodinpos(lift0,prm1,[]),prm1)],[member(rodinpos(lift0,grd1,[]),identifier(none,prm1),identifier(none,levels))],[],[assign(rodinpos(lift0,act1,[]),[identifier(none,level)],[identifier(none,prm1)])],[])])])],[event_b_context(none,levels,[extends(none,[]),constants(none,[identifier(none,'L0'),identifier(none,'L1'),identifier(none,'L2'),identifier(none,'L3'),identifier(none,down),identifier(none,up)]),abstract_constants(none,[]),axioms(none,[partition(rodinpos(levels,axm1,[]),identifier(none,levels),[set_extension(none,[identifier(none,'L0')]),set_extension(none,[identifier(none,'L1')]),set_extension(none,[identifier(none,'L2')]),set_extension(none,[identifier(none,'L3')])]),equal(rodinpos(levels,axm2,[]),identifier(none,up),set_extension(none,[couple(none,[identifier(none,'L0'),identifier(none,'L1')]),couple(none,[identifier(none,'L1'),identifier(none,'L2')]),couple(none,[identifier(none,'L2'),identifier(none,'L3')])])),equal(rodinpos(levels,axm3,[]),identifier(none,down),set_extension(none,[couple(none,[identifier(none,'L1'),identifier(none,'L0')]),couple(none,[identifier(none,'L2'),identifier(none,'L1')]),couple(none,[identifier(none,'L3'),identifier(none,'L2')])]))]),theorems(none,[]),sets(none,[deferred_set(none,levels)])])],[exporter_version(3),po(lift0,'Well-definedness of action',[event(down),action(act1)],true),po(lift0,'Well-definedness of action',[event(up),action(act1)],true)],_Error)).")

s = api.eventb_load(dir+File.separator+"Lift"+File.separator+"lift0.eventb")
assert s != null // it is possible to load it again

"Load the Event-B Lift example and store it to a .eventb file."

