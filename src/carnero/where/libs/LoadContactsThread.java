package carnero.where.libs;

import android.os.Handler;
import carnero.where.models.Contact;
import carnero.where.models.OverlayContacts;
import java.util.ArrayList;

public class LoadContactsThread extends Thread {
	private Core core;
	private OverlayContacts overlay;
	private Handler handler;
	
	private ArrayList<Contact> contacts = null;

	public LoadContactsThread(Core cr, OverlayContacts ovr, Handler hnd) {
		core = cr;
		handler = hnd;
		overlay = ovr;
	}

	@Override
	public void run() {
		contacts = core.getContacts(handler);
		
		if (overlay != null) {
			overlay.fill(contacts);
		}
		
		handler.sendEmptyMessage(0);
	}
}
