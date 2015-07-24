package com.cp.hackathon.trainingcamp;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import com.example.trainingcamp.R;
import com.interaxon.libmuse.ConnectionState;
import com.interaxon.libmuse.Eeg;
import com.interaxon.libmuse.LibMuseVersion;
import com.interaxon.libmuse.Muse;
import com.interaxon.libmuse.MuseArtifactPacket;
import com.interaxon.libmuse.MuseConnectionListener;
import com.interaxon.libmuse.MuseConnectionPacket;
import com.interaxon.libmuse.MuseDataListener;
import com.interaxon.libmuse.MuseDataPacket;
import com.interaxon.libmuse.MuseDataPacketType;
import com.interaxon.libmuse.MuseFileFactory;
import com.interaxon.libmuse.MuseFileWriter;
import com.interaxon.libmuse.MuseManager;
import com.interaxon.libmuse.MusePreset;
import com.interaxon.libmuse.MuseVersion;



import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;
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
    private Muse muse = null;
    private ConnectionListener connectionListener = null;
    private DataListener dataListener = null;
    private boolean dataTransmission = true;
    private MuseFileWriter fileWriter = null;
		    
		    
	
    public MainActivity() {
    	  WeakReference<Activity> weakActivity = new WeakReference<Activity>(this);
          connectionListener = new ConnectionListener(weakActivity);
          dataListener = new DataListener(weakActivity);
	}


	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        inicializeGPSListener();
        parallelTasks();
        //Necessário para usar Runable na activity?
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        
        File dir = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
        fileWriter = MuseFileFactory.getMuseFileWriter(new File(dir, "new_muse_file.muse"));
	    Log.i("Muse Headband", "libmuse version=" + LibMuseVersion.SDK_VERSION);
	    fileWriter.addAnnotationString(1, "MainActivity onCreate");
	    dataListener.setFileWriter(fileWriter);
        connectMuse();
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
		
		private void publishProgress(String param) {

			final String resultado = param;

			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					try {
					//	updateResults(resultado);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
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
			
			 muse.disconnect(true);
			
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
		//Toast.makeText(getApplicationContext(), "[TP] Your location is - \nLat: " + latitudeString + "\nLong: " + longitudeString, Toast.LENGTH_LONG).show();
	} 
	
	
	
	/**
	 * conection muse 
	 */
	
	/**
     * Connection listener updates UI with new connection status and logs it.
     */
    class ConnectionListener extends MuseConnectionListener {

        final WeakReference<Activity> activityRef;

        ConnectionListener(final WeakReference<Activity> activityRef) {
            this.activityRef = activityRef;
        }

        @Override
        public void receiveMuseConnectionPacket(MuseConnectionPacket p) {
            final ConnectionState current = p.getCurrentConnectionState();
            final String status = p.getPreviousConnectionState().toString() +
                         " -> " + current;
            final String full = "Muse " + p.getSource().getMacAddress() +
                                " " + status;
            Log.i("Muse Headband", full);
            Activity activity = activityRef.get();
            // UI thread is used here only because we need to update
            // TextView values. You don't have to use another thread, unless
            // you want to run disconnect() or connect() from connection packet
            // handler. In this case creating another thread is required.
            if (activity != null) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView statusText =
                                (TextView) findViewById(R.id.con_status);
                        statusText.setText(status);
                        TextView museVersionText =
                                (TextView) findViewById(R.id.version);
                        if (current == ConnectionState.CONNECTED) {
                            MuseVersion museVersion = muse.getMuseVersion();
                            String version = museVersion.getFirmwareType() +
                                 " - " + museVersion.getFirmwareVersion() +
                                 " - " + Integer.toString(
                                    museVersion.getProtocolVersion());
                            museVersionText.setText(version);
                        } else {
                            museVersionText.setText(R.string.undefined);
                        }
                    }
                });
            }
        }
    }
    
    /**
     * Data listener will be registered to listen for: Accelerometer,
     * Eeg and Relative Alpha bandpower packets. In all cases we will
     * update UI with new values.
     * We also will log message if Artifact packets contains "blink" flag.
     * DataListener methods will be called from execution thread. If you are
     * implementing "serious" processing algorithms inside those listeners,
     * consider to create another thread.
     */
    class DataListener extends MuseDataListener {

        final WeakReference<Activity> activityRef;
        private MuseFileWriter fileWriter;

        DataListener(final WeakReference<Activity> activityRef) {
            this.activityRef = activityRef;
        }

        @Override
        public void receiveMuseDataPacket(MuseDataPacket p) {
            switch (p.getPacketType()) {
                case ALPHA_RELATIVE:
                    updateAlphaRelative(p.getValues());
                    break;
                case BATTERY:
                    fileWriter.addDataPacket(1, p);
                    // It's library client responsibility to flush the buffer,
                    // otherwise you may get memory overflow. 
                    if (fileWriter.getBufferedMessagesSize() > 8096)
                        fileWriter.flush();
                    break;
                default:
                    break;
            }
        }

        @Override
        public void receiveMuseArtifactPacket(MuseArtifactPacket p) {
            if (p.getHeadbandOn() && p.getBlink()) {
                Log.i("Artifacts", "blink");
            }
        }

        private void updateAlphaRelative(final ArrayList<Double> data) {
            Activity activity = activityRef.get();
            if (activity != null) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                         TextView elem1 = (TextView) findViewById(R.id.elem1);
                         TextView elem2 = (TextView) findViewById(R.id.elem2);
                         TextView elem3 = (TextView) findViewById(R.id.elem3);
                         TextView elem4 = (TextView) findViewById(R.id.elem4);
                         elem1.setText(String.format(
                            "%6.2f", data.get(Eeg.TP9.ordinal())));
                         elem2.setText(String.format(
                            "%6.2f", data.get(Eeg.FP1.ordinal())));
                         elem3.setText(String.format(
                            "%6.2f", data.get(Eeg.FP2.ordinal())));
                         elem4.setText(String.format(
                            "%6.2f", data.get(Eeg.TP10.ordinal())));
                    }
                });
            }
        }

        public void setFileWriter(MuseFileWriter fileWriter) {
            this.fileWriter  = fileWriter;
        }
    }
    
    public void connectMuse() {
    	 MuseManager.refreshPairedMuses();
    	 List<Muse> pairedMuses = MuseManager.getPairedMuses();
         if (pairedMuses.size() < 1) {
             Log.w("Muse Headband", "There is nothing to connect to");
         }
         else {
             muse = pairedMuses.get(0);
             ConnectionState state = muse.getConnectionState();
             if (state == ConnectionState.CONNECTED ||
                 state == ConnectionState.CONNECTING) {
                 Log.w("Muse Headband",
                 "doesn't make sense to connect second time to the same muse");
                 return;
             }
             configureLibrary();
             fileWriter.open();
             fileWriter.addAnnotationString(1, "Connected");
             /**
              * In most cases libmuse native library takes care about
              * exceptions and recovery mechanism, but native code still
              * may throw in some unexpected situations (like bad bluetooth
              * connection). Print all exceptions here.
              */
             try {
                 muse.runAsynchronously();
             } catch (Exception e) {
                 Log.e("Muse Headband", e.toString());
             }
         }
	}
    private void configureLibrary() {
        muse.registerConnectionListener(connectionListener);
        muse.registerDataListener(dataListener,
                                  MuseDataPacketType.ACCELEROMETER);
        muse.registerDataListener(dataListener,
                                  MuseDataPacketType.EEG);
        muse.registerDataListener(dataListener,
                                  MuseDataPacketType.ALPHA_RELATIVE);
        muse.registerDataListener(dataListener,
                                  MuseDataPacketType.ARTIFACTS);
        muse.registerDataListener(dataListener,
                                  MuseDataPacketType.BATTERY);
        muse.setPreset(MusePreset.PRESET_14);
        muse.enableDataTransmission(dataTransmission);
    }

}