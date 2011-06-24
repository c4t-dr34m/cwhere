package carnero.where.models;

import android.graphics.Bitmap;
import java.util.ArrayList;

public class Contact {
	
	public Long id;
	
	public String nameDisplay;
	
	public Bitmap avatar;
	
	public ArrayList<Address> addresses = new ArrayList<Address>();
	
	public Contact(long id, String nameDisplay) {
		this.id = id;
		this.nameDisplay = nameDisplay;
	}
}
