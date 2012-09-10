package xfel.mods.debug;

import dan200.computer.api.IComputerAccess;

public class DummyComputer implements IComputerAccess {

	@Override
	public int createNewSaveDir(String subPath) {
		return 0;
	}

	@Override
	public String mountSaveDir(String desiredLocation, String subPath, int id,
			boolean readOnly) {
		return desiredLocation;
	}

	@Override
	public String mountSaveDir(String desiredLocation, String subPath, int id,
			boolean readOnly, long spaceLimit) {
		return desiredLocation;
	}

	@Override
	public String mountFixedDir(String desiredLocation, String path,
			boolean readOnly) {
		return desiredLocation;
	}

	@Override
	public String mountFixedDir(String desiredLocation, String path,
			boolean readOnly, long spaceLimit) {
		return desiredLocation;
	}

	@Override
	public void unmount(String location) {
		
	}

	@Override
	public int getID() {
		return 24;
	}

	@Override
	public void queueEvent(String event) {
		System.out.println("Event: "+event);
	}

	@Override
	public void queueEvent(String event, Object[] arguments) {
		System.out.println("Event: "+event);
	}

}
