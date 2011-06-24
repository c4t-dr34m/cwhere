package carnero.where.libs;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.ContactsContract;
import android.view.View;
import android.view.View.OnClickListener;
import carnero.where.models.OverlayContacts;

public class ContactDetail implements OnClickListener {
	
	private Context context;
	private OverlayContacts overlay;
	private long contactId;
	
	public ContactDetail(Context ctx, OverlayContacts ovr, long id) {
		context = ctx;
		overlay = ovr;
		contactId = id;
	}
	
	@Override
	public void onClick(View v) {
		final Intent intent = new Intent(Intent.ACTION_VIEW);
		final Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, String.valueOf(contactId));
		intent.setData(uri);

		context.startActivity(intent);
		overlay.hidePopup();
	}
}
