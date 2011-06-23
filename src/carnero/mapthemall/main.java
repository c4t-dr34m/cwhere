package carnero.mapthemall;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationManager;
import carnero.mapthemall.libs.Core;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import carnero.mapthemall.libs.Constants;
import carnero.mapthemall.libs.GeoListener;
import carnero.mapthemall.libs.LoadContactsThread;
import carnero.mapthemall.models.OverlayContacts;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import java.util.List;

public class main extends MapActivity {
	
	private Resources res = null;
	private Core core = null;
	
	private LoadContactsThread loadContactsThread = null;
	private ProgressDialog loadProgress = null;
	
	private MapView mapView = null;
	private MapController mapController = null;	
	private OverlayContacts overlay = null;
	
	private LocationManager geoManager = null;
	private GeoListener geoListener = null;
	
	private Handler loadHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			final int what = msg.what;
			final Bundle msgData = msg.getData();
			
			if (what == 0) {
				mapView.invalidate();
				
				if (loadProgress != null) {
					loadProgress.dismiss();
					loadProgress = null;
				}
			} else if (msgData != null) {
				final String info = msgData.getString("info");
				
				if (info.length() < 24) {
					loadProgress.setMessage(info);
				} else {
					loadProgress.setMessage(info.substring(0, 22) + "...");
				}
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.map);
		
		res = getResources();
		core = new Core(this);
		
		initMap();
		
		if (loadProgress == null) {
			loadProgress = ProgressDialog.show(this, res.getString(R.string.loading_title), res.getString(R.string.loading_message), true);
			loadProgress.setIndeterminate(true);
			loadProgress.setCancelable(true);

			loadContactsThread = new LoadContactsThread(core, overlay, loadHandler);
			loadContactsThread.start();
		}
	}
	
	@Override
	public boolean isRouteDisplayed() {
		return false;
	}
	
	private void initMap() {
		mapView = (MapView) findViewById(R.id.map);

		mapView.setBuiltInZoomControls(true);
		mapView.displayZoomControls(true);
		mapView.setSatellite(false);
		mapView.preLoad();

		// initialize overlays
		final List<Overlay> overlays = mapView.getOverlays();
		overlays.clear();
		
		if (overlay == null) {
			overlay = new OverlayContacts(this, getResources().getDrawable(R.drawable.ic_contact_picture_3));
			
			overlays.add(overlay);
		}
		
		mapView.invalidate();

		mapController = mapView.getController();
		mapController.setZoom(Constants.default_mapZoom);
		
		geoManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		geoListener = new GeoListener(this);
		
		try {
			geoManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, geoListener);
		} catch (Exception e) {
			Log.w(Constants.tag, "There is no NETWORK location provider");
		}
	}
	
	public void onLocationChanged(Location location) {
		if (location != null && mapController != null) {
			final GeoPoint gpCenter = new GeoPoint((int) (location.getLatitude() * 1e6), (int) (location.getLongitude() * 1e6));
			mapController.animateTo(gpCenter);
			
			geoManager.removeUpdates(geoListener);
		}
	}
}