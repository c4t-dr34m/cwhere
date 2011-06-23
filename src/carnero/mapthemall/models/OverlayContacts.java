package carnero.mapthemall.models;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.widget.Toast;
import carnero.mapthemall.R;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.Projection;
import java.util.ArrayList;

public class OverlayContacts extends ItemizedOverlay<AddressOI> {

	private Context context;
	private ArrayList<AddressOI> addresses = new ArrayList<AddressOI>();
	
	private Bitmap badge = null;
	private PaintFlagsDrawFilter setfil = null;
	private PaintFlagsDrawFilter remfil = null;
	
	public OverlayContacts(Context ctx, Drawable defaultPin) {
		super(boundCenterBottom(defaultPin));

		context = ctx;
	}

	public synchronized void fill(ArrayList<Contact> contacts) {
		if (contacts == null) {
			return;
		}

		ArrayList<AddressOI> addressesPre = new ArrayList<AddressOI>();

		for (Contact contact : contacts) {
			if (contact.addresses == null || contact.addresses.isEmpty()) {
				continue;
			}

			for (Address addr : contact.addresses) {
				if (addr.overlayItem != null) {
					addr.overlayItem.setMarker(boundCenterBottom(addr.overlayItem.getMarker(0)));
					addressesPre.add(addr.overlayItem);
				}
			}
		}

		addresses.clear();

		if (!addressesPre.isEmpty()) {
			addresses = (ArrayList<AddressOI>) addressesPre.clone();
		}

		setLastFocusedIndex(-1); // to reset tap during data change
		populate();
	}

	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		super.draw(canvas, mapView, shadow);
	}

	@Override
	public boolean onTap(int index) {
		if (addresses != null && index < addresses.size()) {
			final Toast toast = Toast.makeText(context, addresses.get(index).getContact().nameDisplay, Toast.LENGTH_LONG);

			toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 0, 100);
			toast.show();
		}

		return false;
	}

	@Override
	public AddressOI createItem(int index) {
		try {
			return addresses.get(index);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public int size() {
		try {
			return addresses.size();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return 0;
	}
}
