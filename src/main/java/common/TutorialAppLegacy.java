package common;

public abstract class TutorialAppLegacy extends TutorialApp {
	protected TutorialAppLegacy() {
		
	}
	
	protected TutorialAppLegacy(boolean fullscreen) {
		super(fullscreen);
	}
	
	protected void init() {}
	protected void loop() {}

	public void run() {
		init();
		while(advanceNextFrame()) {
			loop();
		}
	}
}
