package carnero.mapthemall.libs;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import carnero.mapthemall.main;

public class GeoListener implements LocationListener {
	
	public main parent;
	public Location location;
	
	public GeoListener(main prnt)  {
		parent = prnt;
	}
	
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// nothing
	}

	@Override
	public void onLocationChanged(Location loc) {
		location = loc;
		
		parent.onLocationChanged(loc);
	}
	
	@Override
	public void onProviderDisabled(String provider) {
		// nothing
	}

	@Override
	public void onProviderEnabled(String provider) {
		// nothing
	}
}
