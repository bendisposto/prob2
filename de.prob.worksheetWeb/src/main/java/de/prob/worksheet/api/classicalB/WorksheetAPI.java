package de.prob.worksheet.api.classicalB;

import java.util.List;

/*import de.prob.model.classicalb.ClassicalBFactory;
import de.prob.model.classicalb.ClassicalBModel;
import de.prob.model.representation.AbstractModel;
import de.prob.scripting.FactoryProvider;
import de.prob.statespace.AnimationSelector;
import de.prob.statespace.History;
import de.prob.webconsole.ServletContextListener;
*/

public class WorksheetAPI {
	/*	private final Injector INJECTOR;
	private FactoryProvider factoryProvider;
	private AnimationSelector animations;
	*/private List<WorksheetAPIListener>	listeners;	/*
														
														@Inject
														public WorksheetAPI(FactoryProvider factoryProvider,AnimationSelector animations) {
														INJECTOR=ServletContextListener.INJECTOR;
														this.factoryProvider=factoryProvider;
														this.animations=animations;
														this.listeners=new ArrayList<WorksheetAPIListener>();
														}
														
														public void getHistories(){
														notifyApiListeners(WorksheetAPIListenerEvent.TYPE_OUTPUT, WorksheetAPIListenerEvent.NAME_ANIMATION, new Object[]{"Histories:",animations.getHistories()});
														}
														public void getSelectedHistory(){
														notifyApiListeners(WorksheetAPIListenerEvent.TYPE_OUTPUT, WorksheetAPIListenerEvent.NAME_ANIMATION, new Object[]{"current History:",animations.getCurrentHistory()});	
														}
														public void initialize(){
														History history=this.animations.getCurrentHistory();
														History newHistory=history.add(0);
														animations.currentStateChanged(history, newHistory);
														}
														public void anyEvent(){
														History oldHistory=this.animations.getCurrentHistory();
														History newHistory=oldHistory.anyEvent(null);
														animations.currentStateChanged(oldHistory, newHistory);
														}
														public void load(){
														String url="C:\\Users\\Rene\\prob2repo\\prob2\\de.prob.core.kernel\\src\\main\\java\\de\\prob\\scripting\\examples\\scheduler.mch";
														this.load(url);	
														}
														public void load(String url) {
														try {
														
														//load the file;
														File bFile=loadFile(url);
														
														// get Factory and load the model from the file
														ClassicalBFactory factory= factoryProvider.getClassicalBFactory();
														ClassicalBModel model=factory.load(bFile);

														// create a History for the model
														History history=newHistory(model);
														
														// add the History to the animation selector;
														addHistoryToAnimation(history);
														
														// TODO maybe remove and replace by explicit console call of WorksheetAPI.selectHistory() 
														//selectHistory(history);
														
														} catch (FileNotFoundException e) {
														e.printStackTrace();
														} catch (IOException e) {
														e.printStackTrace();
														} catch (BException e) {
														notifyApiListeners(WorksheetAPIListenerEvent.TYPE_ERROR,WorksheetAPIListenerEvent.NAME_API,new Object[]{"BException while loading from file",url});
														e.printStackTrace();
														}
														}
														
														public History selectHistory(String index){
														int intIndex;
														try{
														intIndex=Integer.parseInt(index);
														return selectHistory(intIndex);
														}catch(NumberFormatException e){
														notifyApiListeners(WorksheetAPIListenerEvent.TYPE_ERROR, WorksheetAPIListenerEvent.NAME_ANIMATION, new Object[]{"The index is not a number"});
														}
														return null;
														
														}
														

														private History newHistory(AbstractModel model){
														History history=new History(model);
														notifyApiListeners(WorksheetAPIListenerEvent.TYPE_ACTION,WorksheetAPIListenerEvent.NAME_API,new Object[]{"new History created from statespace",history});
														return history;
														}
														
														private void addHistoryToAnimation(History history){
														animations.addNewHistory(history);
														notifyApiListeners(WorksheetAPIListenerEvent.TYPE_ACTION,WorksheetAPIListenerEvent.NAME_API,new Object[]{"new History added to Animations",animations});
														return;		
														}
														private History selectHistory(int index){
														if(index>=animations.getHistories().size()){
														notifyApiListeners(WorksheetAPIListenerEvent.TYPE_ERROR, WorksheetAPIListenerEvent.NAME_ANIMATION, new Object[]{"no animations with the specified index exists"});
														return null;
														}
														History newHistory=this.animations.getHistories().get(index);
														
														this.selectHistory(newHistory);
														
														return newHistory;		
														}
														private void selectHistory(History history){
														animations.changeCurrentHistory(history);
														notifyApiListeners(WorksheetAPIListenerEvent.TYPE_ACTION,WorksheetAPIListenerEvent.NAME_API,new Object[]{"selected another history",animations});
														}
														
														private File loadFile(String url) throws FileNotFoundException,IOException{
														assert(url!=null);
														File bFile=new File(url);
														
														if(!bFile.exists()){
														notifyApiListeners(WorksheetAPIListenerEvent.TYPE_ERROR,WorksheetAPIListenerEvent.NAME_API,new Object[]{"File doesn't exist",url});
														throw new FileNotFoundException();
														}
														
														if(!bFile.canRead()){
														notifyApiListeners(WorksheetAPIListenerEvent.TYPE_ERROR,WorksheetAPIListenerEvent.NAME_API,new Object[]{"File can't be read",url});
														throw new IOException();
														}
														
														return bFile;
														}
														
														
														*/

	// TODO extend data so that Api Calls can send more complex information
	public void notifyApiListeners(final int type, final int name, final Object[] data) {
		final WorksheetAPIListenerEvent event = new WorksheetAPIListenerEvent();
		event.data = data;
		event.type = type;
		event.name = name;
		for (final WorksheetAPIListener listener : this.listeners) {
			listener.notify(event);
		}
	}

	public void addApiListener(final WorksheetAPIListener listener) {
		assert (this.listeners != null);
		assert (listener != null);

		this.listeners.add(listener);

	}

	public void removeApiListener(final WorksheetAPIListener listener) {
		this.listeners.remove(listener);
	}
}
