package carnero.mapthemall.libs;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import carnero.mapthemall.models.OverlayContacts;
import com.google.android.maps.Overlay;
import java.util.List;

public class MapView extends com.google.android.maps.MapView {
	
	public MapView(Context ctx, AttributeSet attrs) {
		super(ctx, attrs);
	}
	
	public MapView(Context ctx, AttributeSet attrs, int defStyle) {
		super(ctx, attrs, defStyle);
	}
	
	public MapView(Context ctx, String key) {
		super(ctx, key);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		final int action = ev.getAction();
		
		if (action == MotionEvent.ACTION_DOWN) {
			final List<Overlay> overlays = getOverlays();
			if (overlays != null && !overlays.isEmpty()) {
				for (Overlay overlay : overlays) {
					if (overlay instanceof OverlayContacts) {
						((OverlayContacts) overlay).hidePopup();
					}
				}
			}
		}
		
		return super.onTouchEvent(ev);
	}
}
