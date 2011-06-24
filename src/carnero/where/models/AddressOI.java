package carnero.where.models;

import carnero.where.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import carnero.where.libs.Core;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

public class AddressOI extends OverlayItem{
	
	private Context context;
	private Contact contact;
	private int addressIndex;
	private float ratio = 1.0f;
	private int avatarSize = 48;
	
	private Drawable marker = null;
	
	public AddressOI(Context ctx, Contact ctc, int addrIndex, GeoPoint geoPoint) {
		super(geoPoint, ctc.nameDisplay, "");
		
		context = ctx;
		contact = ctc;
		addressIndex = addrIndex;
		ratio = context.getResources().getDisplayMetrics().density;
		avatarSize = (int) (48 * ratio);
	}
	
	@Override
	public Drawable getMarker(int state) {
		if (marker == null) {
			Bitmap badge = BitmapFactory.decodeResource(context.getResources(), R.drawable.badge);
			badge = badge.copy(Bitmap.Config.ARGB_8888, true); // to create mutable bitmap (needed for canvas)
			
			Bitmap avatar;
			if (contact.avatar == null) {
				avatar = BitmapFactory.decodeResource(context.getResources(), R.drawable.contact_generic);
			} else {
				avatar = contact.avatar;
			}
			avatar = Core.resizeBitmap(avatar, avatarSize, avatarSize);
			
			final int avatarWidth = avatar.getWidth();
			final int avatarHeight = avatar.getHeight();
			float top = 10f * ratio;
			float left = 10f * ratio;
			
			if (avatarWidth < avatarSize) {
				top += ((avatarSize - avatarWidth) / 2);
			}
			if (avatarHeight < avatarSize) {
				top += ((avatarSize - avatarHeight) / 2);
			}
			
			Canvas badgeCanvas = new Canvas(badge);
			badgeCanvas.drawBitmap(avatar, left, top, null); 
			badgeCanvas.save();
			
			marker = new BitmapDrawable(badge);
			marker.setBounds(0, 0, marker.getIntrinsicWidth(), marker.getIntrinsicHeight());
		}

		setMarker(marker);

		return marker;
	}
	
	public Contact getContact() {
		return contact;
	}
	
	public Address getAddress() {
		return contact.addresses.get(addressIndex);
	}
}
