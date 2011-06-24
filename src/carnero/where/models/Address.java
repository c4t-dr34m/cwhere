package carnero.where.models;

import android.provider.ContactsContract.CommonDataKinds.StructuredPostal;

public class Address {
	
	public int type = StructuredPostal.TYPE_OTHER;
	
	public String street;
	public String city;
	public String postcode;
	public String region;
	public String country;
	
	public Double latitude;
	public Double longitude;
	
	public AddressOI overlayItem;
}
