package carnero.mapthemall.libs;

import carnero.mapthemall.models.Contact;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.StructuredPostal;
import android.util.Log;
import carnero.mapthemall.models.Address;
import carnero.mapthemall.models.AddressOI;
import com.google.android.maps.GeoPoint;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Core {

	private Context context;
	
	public Core(Context ctx) {
		context = ctx;
	}
	
	public ArrayList<Contact> getContacts(Handler handler) {
		final ArrayList<Contact> contacts = new ArrayList<Contact>();
		final ContentResolver resolver = context.getContentResolver();
		final String[] projection = new String[]{
			ContactsContract.Contacts._ID,
			ContactsContract.Contacts.DISPLAY_NAME};
		
		final Cursor cursor = resolver.query(ContactsContract.Contacts.CONTENT_URI, projection, null, null, null);
		
		try {
			while (cursor.moveToNext()) {
				long id = cursor.getLong(cursor.getColumnIndex(ContactsContract.Contacts._ID));
				String nameDisplay = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

				Contact contact = new Contact(id, nameDisplay);
				contacts.add(contact);
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		
		for (Contact contact : contacts) {
			final Bundle msgData = new Bundle();
			msgData.putString("info", contact.nameDisplay);
			
			final Message msg = new Message();
			msg.what = 1;
			msg.setData(msgData);
			
			handler.sendMessage(msg);
			
			putAddress(resolver, contact);
			putAvatar(resolver, contact);
		}

		return contacts;
	}

	private void putAddress(ContentResolver resolver, Contact contact) {
		if (contact == null || contact.id == null) {
			return;
		}

		final String where = ContactsContract.Data.CONTACT_ID + " = " + contact.id + " AND ContactsContract.Data.MIMETYPE = '" + ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE + "'";

		final String[] projection = new String[]{
			StructuredPostal.TYPE,
			StructuredPostal.STREET,
			StructuredPostal.CITY,
			StructuredPostal.POSTCODE,
			StructuredPostal.REGION,
			StructuredPostal.COUNTRY};

		Cursor cursor = resolver.query(ContactsContract.Data.CONTENT_URI, projection, where, null, null);
		
		try {
			while (cursor.moveToNext()) {
				final Address addr = new Address();
				
				addr.type = cursor.getInt(cursor.getColumnIndex(StructuredPostal.TYPE));
				addr.street = cursor.getString(cursor.getColumnIndex(StructuredPostal.STREET));
				addr.city = cursor.getString(cursor.getColumnIndex(StructuredPostal.CITY));
				addr.postcode = cursor.getString(cursor.getColumnIndex(StructuredPostal.POSTCODE));
				addr.region = cursor.getString(cursor.getColumnIndex(StructuredPostal.REGION));
				addr.country = cursor.getString(cursor.getColumnIndex(StructuredPostal.COUNTRY));
				putLocation(addr);

				if (addr.latitude != null && addr.longitude != null) {
					final GeoPoint gp = new GeoPoint((int) (addr.latitude * 1e6), (int) (addr.longitude * 1e6));
					
					addr.overlayItem = new AddressOI(context, contact, contact.addresses.size(), gp);
				}

				contact.addresses.add(addr);
			}
		} finally {		
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	private void putAvatar(ContentResolver resolver, Contact contact) {
		if (contact == null || contact.id == null) {
			return;
		}
		
		final Uri uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contact.id);
		
		try {
			final InputStream is = ContactsContract.Contacts.openContactPhotoInputStream(resolver, uri);

			if (is == null) {
				return;
			}

			contact.avatar = BitmapFactory.decodeStream(is);

			is.close();
		} catch (IOException e) {
			// failed to read image... and what?
		}
	}
	
	private void putLocation(Address address) {
		final Geocoder geocoder = new Geocoder(context, Locale.getDefault());
		
		final StringBuilder sb = new StringBuilder();
		if (address.street != null) {
			if (sb.length() > 0) {
				sb.append(", ");
			}
			sb.append(address.street);
		}
		if (address.city != null) {
			if (sb.length() > 0) {
				sb.append(", ");
			}
			if (address.postcode != null) {
				sb.append(address.postcode);
				sb.append(" ");
			}
			sb.append(address.city);
		}
		if (address.country != null) {
			if (sb.length() > 0) {
				sb.append(", ");
			}
			sb.append(address.country);
		}
		
		List<android.location.Address> locations = null;
		try {
			locations = geocoder.getFromLocationName(sb.toString(), 1);
		} catch (Exception e) {
			Log.e(Constants.tag, "Failed to get address' coordinates: " + e.toString());
		}
		
		if (locations == null || locations.isEmpty()) {
			return;
		}
		
		android.location.Address loc = locations.get(0);
		
		if (!loc.hasLatitude() || !loc.hasLongitude()) {
			return;
		}
		
		address.latitude = loc.getLatitude();
		address.longitude = loc.getLongitude();
	}
	
	public static Bitmap resizeBitmap(Bitmap image, int width, int height) {
		int w = image.getWidth();
		int h = image.getHeight();

		float ratio = 1.0f;

		if (w > width || h > height) {
			if ((width / w) > (height / h)) {
				ratio = (float) height / (float) h;
			} else {
				ratio = (float) width / (float) w;
			}
			
			final int newWidth = (int) Math.ceil(w * ratio);
			final int newHeight = (int) Math.ceil(h * ratio);

			Bitmap resizedImage = Bitmap.createScaledBitmap(image, newWidth, newHeight, true);

			return resizedImage;
		}
		
		return image;
	}
}