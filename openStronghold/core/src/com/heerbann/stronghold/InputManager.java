package com.heerbann.stronghold;

import java.util.Comparator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.gdx.utils.Pools;

public class InputManager implements InputProcessor{
	
	public InputManager(){
		Gdx.input.setInputProcessor(this);
	}

	private final InputProcessorWrapperComparator comperator = new InputProcessorWrapperComparator();
	private final Array<InputProcessorWrapper> processors = new Array<InputProcessorWrapper>();
	public static class InputProcessorWrapper implements Poolable{

		public InputProcessor input = null;
		public int priority = 0;
		public boolean interrupting = true;
		
		@Override
		public void reset() {
			input = null;
			priority = 0;
		}
	}
	
	public void addInputProcessor(InputProcessor input){
		addInputProcessor(processors.size, true, input);
	}
	
	public void addInputProcessor(int priority, InputProcessor input){
		addInputProcessor(priority, true, input);
	}
	
	
	public void addInputProcessor(int priority, boolean interrupting, InputProcessor input){
		for(int i = 0; i < processors.size; i++){
			if(processors.get(i).input.equals(input)){
				processors.get(i).interrupting = interrupting;
				processors.get(i).priority = priority;
				processors.sort(comperator);
				return;
			}
		}		
		InputProcessorWrapper i = Pools.obtain(InputProcessorWrapper.class);
		i.input = input;
		i.interrupting = interrupting;
		i.priority = priority;
		processors.add(i);
		processors.sort(comperator);
	}
	
	public boolean removeInputProcessor(InputProcessor input){
		int index = 0;
		InputProcessorWrapper w = null;
		for(index = 0; index < processors.size; index++){
			w = processors.get(index);
			if(w.input.equals(input)) break;
			w = null;
		}
		if(w != null){
			processors.removeIndex(index);
			Pools.free(w);
			processors.sort(comperator);
			return true;
		}
		return false;			
	}

	@Override
	public boolean keyDown(int keycode) {
		for(int i = 0; i < processors.size; i++){
			InputProcessorWrapper w = processors.get(i);
			if(w.input.keyDown(keycode) && w.interrupting) return true;
		}
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		for(int i = 0; i < processors.size; i++){
			InputProcessorWrapper w = processors.get(i);
			if(w.input.keyUp(keycode) && w.interrupting) return true;
		}
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		for(int i = 0; i < processors.size; i++){
			InputProcessorWrapper w = processors.get(i);
			if(w.input.keyTyped(character) && w.interrupting) return true;
		}
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		for(int i = 0; i < processors.size; i++){
			InputProcessorWrapper w = processors.get(i);
			if(w.input.touchDown(screenX, screenY, pointer, button) && w.interrupting) return true;
		}
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		for(int i = 0; i < processors.size; i++){
			InputProcessorWrapper w = processors.get(i);
			if(w.input.touchUp(screenX, screenY, pointer, button) && w.interrupting) return true;
		}
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		for(int i = 0; i < processors.size; i++){
			InputProcessorWrapper w = processors.get(i);
			if(w.input.touchDragged(screenX, screenY, pointer) && w.interrupting) return true;
		}
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		for(int i = 0; i < processors.size; i++){
			InputProcessorWrapper w = processors.get(i);
			if(w.input.mouseMoved(screenX, screenY) && w.interrupting) return true;
		}
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		for(int i = 0; i < processors.size; i++){
			InputProcessorWrapper w = processors.get(i);
			if(w.input.scrolled(amount) && w.interrupting) return true;
		}
		return false;
	}
	
	public static class InputProcessorWrapperComparator implements Comparator<InputProcessorWrapper>{
		@Override
		public int compare(InputProcessorWrapper a, InputProcessorWrapper b) {
			return a.priority > b.priority ? 1 : (a.priority == b.priority) ? 0 : -1;
		}
	}

}
