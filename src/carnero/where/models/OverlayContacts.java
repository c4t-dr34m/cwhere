package carnero.where.models;

import carnero.where.R;
import carnero.where.libs.MapView;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import carnero.where.libs.ContactDetail;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import java.util.ArrayList;

public class OverlayContacts extends ItemizedOverlay<AddressOI> {

	private Context context;
	private MapView mapView;
	private ArrayList<AddressOI> addresses = new ArrayList<AddressOI>();
	
	private View popupView;
	private ViewGroup popupParent;
	
	public OverlayContacts(Context ctx, MapView mv, Drawable defaultPin) {
		super(boundCenterBottom(defaultPin));

		context = ctx;
		mapView = mv;
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
	
	public synchronized int getCount() {
		return addresses.size();
	}

	@Override
	public void draw(Canvas canvas, com.google.android.maps.MapView mapView, boolean shadow) {
		super.draw(canvas, mapView, false);
	}

	@Override
	public boolean onTap(int index) {
		hidePopup();
		
		if (addresses != null && index < addresses.size()) {
			final AddressOI item = getItem(index);
			final GeoPoint geo = item.getPoint();

			showPopup(geo, item);
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

	private void showPopup(GeoPoint point, AddressOI item) {
		if (popupView == null) {
			popupParent = (ViewGroup) mapView.getParent();
			popupView = ((Activity) context).getLayoutInflater().inflate(R.layout.map_popup, popupParent, false);

			MapView.LayoutParams mapViewLP = new MapView.LayoutParams(
					MapView.LayoutParams.WRAP_CONTENT,
					MapView.LayoutParams.WRAP_CONTENT,
					point, -48, -160,
					MapView.LayoutParams.LEFT);

			((TextView) popupView.findViewById(R.id.text)).setText(item.getContact().nameDisplay);

			popupView.setOnClickListener(new ContactDetail(context, this, item.getContact().id));

			mapView.addView(popupView, mapViewLP);
		}
	}
	
	public void hidePopup() {
		if (popupView != null) {
			mapView.removeView(popupView);
			popupView = null;
		}
	}
	
	public boolean isPopupShown() {
		if (popupView != null) {
			return true;
		}
		
		return false;
	}
}
