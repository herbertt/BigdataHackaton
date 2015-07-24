package com.cp.hackathon.trainingcamp;

import com.example.trainingcamp.R;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.app.Activity;
import android.content.Context;
import android.view.Menu;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	private String latitudeString  = "";
	private String longitudeString = "";
	// The minimum distance to change Updates in meters
	private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 0; // 1 meters
	// The minimum time between updates in milliseconds
	private static final long MIN_TIME_BW_UPDATES = 0;//1000 * 60 * 1; // 1 minute
	private LocationManager mlocManager;
	private LocationListener mlocListener;
	private boolean isGPSEnabled = true;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        inicializeGPSListener();
        parallelTasks();
        //Necessário para usar Runable na activity?
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
       
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    
    
    public class DoSomethingThread extends Thread {

		private static final String TAG = "DoSomethingThread";
		private static final int DELAY = 1000; // 1 second

		@Override
		public void run() {
			while (true) {
				//xherman
				//publishProgress(requisicao()); //old code
				try {
				//	publishProgress(fiwareRequest());
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				//
				try {
					Thread.sleep(DELAY);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
			}
		}
    }
    
    
    /*
	 * ########################################################################
	 * ########################################################################
	 *        The next metod are related at GPS position
	 * ########################################################################
	 * ########################################################################
	 */
	public void inicializeGPSListener(){
		
		/* Use the LocationManager class to obtain GPS locations */ 
		mlocManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE); 
		isGPSEnabled = mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		mlocListener = new MyLocationListener(this); 
		//-------------------------------
		/*Criteria criteria = new Criteria();
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(false);
        criteria.setPowerRequirement(Criteria.POWER_LOW);       
         
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        String providerFine = mlocManager.getBestProvider(criteria, true);*/
    	//-------------------------------	
		mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, mlocListener);
		Location location =  mlocManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		
		
		if(location!=null){		
			latitudeString  = String.valueOf(location.getLatitude());
			longitudeString = String.valueOf(location.getLongitude());		
		}else{
			
			mlocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, mlocListener);
	 		location =  mlocManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
	 		
		     if(location!=null){
					latitudeString  = String.valueOf(location.getLatitude());
					longitudeString = String.valueOf(location.getLongitude());
				}
		}

	}

	public String getLatitudeString() {
		return latitudeString;
	}

	public String getLongitudeString() {
		return longitudeString;
	}
	public void setLatitudeString(String latitudeString) {
		this.latitudeString = latitudeString;
	}

	public void setLongitudeString(String longitudeString) {
		this.longitudeString = longitudeString;
	}


	/* Class My Location Listener */ 
	public class MyLocationListener implements LocationListener { 

		private Context lContexto;

		public MyLocationListener(Context contexto){
			this.lContexto = contexto;
		}

		@Override 
		public void onLocationChanged(Location loc) { 
			//Continue listening for a more accurate location
    	    loc.getLatitude(); 
			loc.getLongitude(); 
			
			 if(loc.getAccuracy() <= 10 && loc.getSpeed() <= 12){
		          //Do something
					//Atualizando as informações do app
				   ((MainActivity) lContexto).setLatitudeString(String.valueOf(loc.getLatitude()));
					((MainActivity) lContexto).setLongitudeString(String.valueOf(loc.getLongitude()));

					((MainActivity) lContexto).parallelTasks();
					
		     }
		
		} 


		@Override 
		public void onProviderDisabled(String provider) { 
		} 

		@Override 
		public void onProviderEnabled(String provider) { 
		} 

		@Override 
		public void onStatusChanged(String provider, int status, Bundle extras) { 
		} 

	}


	public void parallelTasks() {
		// TODO Auto-generated method stub
		Toast.makeText(getApplicationContext(), "[TP] Your location is - \nLat: " + latitudeString + "\nLong: " + longitudeString, Toast.LENGTH_LONG).show();
	} 
	

}